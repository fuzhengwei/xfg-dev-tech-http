package cn.bugstack.xfg.dev.tech.job;

import cn.bugstack.chatglm.model.*;
import cn.bugstack.chatglm.session.OpenAiSession;
import cn.bugstack.xfg.dev.tech.job.model.*;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 知识星球回答问题Job
 * @create 2023-10-14 14:16
 */
@Slf4j
@Component()
public class ZSXQJob {

    @Autowired(required = false)
    private OpenAiSession openAiSession;

    private volatile Set<Long> topicIds = new HashSet<>();

    @Value("${chatglm.sdk.config.cookie}")
    private String cookie;

    @Scheduled(cron = "0/10 * * * * ?")
    public void exec() throws Exception {
        Response response = getResponse(cookie);
        RespData respData = response.getRespData();
        List<TopicsItem> topics = respData.getTopics();

        for (TopicsItem topicsItem : topics) {
            // 是否回答过判断
            if (!isCommentDone(topicsItem)) continue;

            // 找到圈我我帖子
            long topicId = topicsItem.getTopicId();
            Talk talk = topicsItem.getTalk();
            // "<e type="mention" uid="241858242255511" title="%40%E5%B0%8F%E5%82%85%E5%93%A5" /> 提问 java 冒泡排序"
            String text = talk.getText();

            // 正在匹配处理
            String regex = "<e type=\"mention\" uid=\"(\\d+)\" title=\"(.*?)\" /> (.*)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                String uid = matcher.group(1);
                String remainingText = matcher.group(3);
                if ("241858242255511".equals(uid)) {

                    if (null == openAiSession) {
                        log.info("你没有开启 ChatGLM 参考yml配置文件来开启");
                        // 你可以使用 ChatGLM SDK 进行回答，回复问题；
                        comment(cookie, topicId, "【测试，只回答圈我的帖子】对接 ChatGLM SDK https://bugstack.cn/md/project/chatgpt/sdk/chatglm-sdk-java.html 回答：" + remainingText);
                    } else {
                        log.info("ChatGLM 进入回答 {} {}", topicId, remainingText);
                        if (topicIds.contains(topicId)){
                            continue;
                        } else {
                            topicIds.add(topicId);
                        }
                        new Thread(() -> {
                            // 入参；模型、请求信息
                            ChatCompletionRequest request = new ChatCompletionRequest();
                            request.setModel(Model.CHATGLM_LITE); // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
                            request.setPrompt(new ArrayList<ChatCompletionRequest.Prompt>() {
                                private static final long serialVersionUID = -7988151926241837899L;

                                {
                                    add(ChatCompletionRequest.Prompt.builder()
                                            .role(Role.user.getCode())
                                            .content(remainingText)
                                            .build());
                                }
                            });

                            // 请求
                            try {
                                StringBuilder content = new StringBuilder();
                                openAiSession.completions(request, new EventSourceListener() {
                                    @Override
                                    public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                                        ChatCompletionResponse chatCompletionResponse = com.alibaba.fastjson.JSON.parseObject(data, ChatCompletionResponse.class);
                                        log.info("测试结果 onEvent：{}", chatCompletionResponse.getData());
                                        // type 消息类型，add 增量，finish 结束，error 错误，interrupted 中断
                                        if (EventType.finish.getCode().equals(type)) {
                                            ChatCompletionResponse.Meta meta = com.alibaba.fastjson.JSON.parseObject(chatCompletionResponse.getMeta(), ChatCompletionResponse.Meta.class);
                                            log.info("[输出结束] Tokens {}", com.alibaba.fastjson.JSON.toJSONString(meta));
                                        }
                                        content.append(chatCompletionResponse.getData());
                                    }

                                    @Override
                                    public void onClosed(EventSource eventSource) {
                                        log.info("对话完成");
                                        // 你可以使用 ChatGLM SDK 进行回答，回复问题；
                                        comment(cookie, topicId, "ChatGLM 回答：" + content);
                                        topicIds.remove(topicId);
                                    }

                                });
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }).start();
                    }
                }
            }
        }

    }

    private boolean isCommentDone(TopicsItem topicsItem) {
        List<ShowCommentsItem> showComments = topicsItem.getShowComments();
        if (null != showComments && !showComments.isEmpty()) {
            for (ShowCommentsItem showCommentsItem : showComments) {
                Owner owner = showCommentsItem.getOwner();
                long userId = owner.getUserId();
                // 如果已经被我回答过，则不回答
                if (241858242255511L == userId) {
                    return false;
                }
            }
        }
        return true;
    }

    private void comment(String cookie, long topicId, String text) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpPost httpPost = new HttpPost("https://api.zsxq.com/v2/topics/" + topicId + "/comments");

        httpPost.setHeader("Accept-Encoding", "deflate, gzip");
        httpPost.setHeader("accept", "application/json, text/plain, */*");
        httpPost.setHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8");
        httpPost.setHeader("authority", "api.zsxq.com");
        httpPost.setHeader("content-type", "application/json");
        httpPost.setHeader("cookie", cookie);
        httpPost.setHeader("dnt", "1");
        httpPost.setHeader("origin", "https://wx.zsxq.com");
        httpPost.setHeader("referer", "https://wx.zsxq.com/");
        httpPost.setHeader("sec-ch-ua", "\"Chromium\";v=\"118\", \"Google Chrome\";v=\"118\", \"Not=A?Brand\";v=\"99\"");
        httpPost.setHeader("sec-ch-ua-mobile", "?0");
        httpPost.setHeader("sec-ch-ua-platform", "\"macOS\"");
        httpPost.setHeader("sec-fetch-dest", "empty");
        httpPost.setHeader("sec-fetch-mode", "cors");
        httpPost.setHeader("sec-fetch-site", "same-site");
        httpPost.setHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36");
        httpPost.setHeader("x-request-id", "162ae5f17-2123-4ae3-67df-8b9775414e0");
        httpPost.setHeader("x-signature", "698895e3ec4e651128b3d16755546bd2bc659687");
        httpPost.setHeader("x-timestamp", "1697257286");
        httpPost.setHeader("x-version", "2.45.0");

        String requestBody = "{\"req_data\":{\"text\":\"" + StringEscapeUtils.escapeJava(text) + "\",\"image_ids\":[],\"mentioned_user_ids\":[]}}";
        try {
            httpPost.setEntity(new StringEntity(requestBody));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);
            log.info("回贴结果 {}", responseString);
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Response getResponse(String cookie) throws Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpGet get = new HttpGet("https://api.zsxq.com/v2/groups/28885518425541/topics?scope=all&count=20");

        get.addHeader("Accept-Encoding", "deflate, gzip");
        get.addHeader("accept", "application/json, text/plain, */*");
        get.addHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8");
        get.addHeader("authority", "api.zsxq.com");
        get.addHeader("cookie", cookie);
        get.addHeader("dnt", "1");
        get.addHeader("origin", "https://wx.zsxq.com");
        get.addHeader("referer", "https://wx.zsxq.com/");
        get.addHeader("sec-ch-ua", "\"Chromium\";v=\"118\", \"Google Chrome\";v=\"118\", \"Not=A?Brand\";v=\"99\"");
        get.addHeader("sec-ch-ua-mobile", "?0");
        get.addHeader("sec-ch-ua-platform", "\"macOS\"");
        get.addHeader("sec-fetch-dest", "empty");
        get.addHeader("sec-fetch-mode", "cors");
        get.addHeader("sec-fetch-site", "same-site");
        get.addHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36");
        get.addHeader("x-request-id", "372177b46-4e7d-9373-d891-98a22adaeb7");
        get.addHeader("x-signature", "32b39b5d1af5995e3b5022e58a8d8f23cd427434");
        get.addHeader("x-timestamp", "1697249698");
        get.addHeader("x-version", "2.45.0");

        CloseableHttpResponse response = httpClient.execute(get);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String jsonStr = EntityUtils.toString(response.getEntity());
            return JSON.parseObject(jsonStr, Response.class);
        } else {
            throw new RuntimeException("Err Code is " + response.getStatusLine().getStatusCode());
        }
    }

}
