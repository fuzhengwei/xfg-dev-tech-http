package cn.bugstack.xfg.dev.tech.test;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description
 * @create 2023-10-14 14:40
 */
public class ChatGLMTest {

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://open.bigmodel.cn/api/paas/v3/model-api/chatglm_lite/sse-invoke");

        // Set headers
        httpPost.setHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiNGUwODdlNDEzNTMwNmVmNGE2NzZmMGNjZTNjZWU1NjAiLCJleHAiOjE2OTcyNjc1MTExNTYsInRpbWVzdGFtcCI6MTY5NzI2NTcxMTE1Nn0.d4f_g9VJF-Aq2kte7GXEA22z2Gqfi8fEbaUb9_2sYO0");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        httpPost.setHeader("Accept", "text/event-stream");

        // Set request body
        String requestBody = "{\"top_p\": 0.7, \"sseFormat\": \"data\", \"temperature\": 0.9, \"incremental\": true, \"request_id\": \"xfg-1696992276607\", \"prompt\": [{\"role\": \"user\", \"content\": \"写个java冒泡排序\"}]}";
        StringEntity requestEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        httpPost.setEntity(requestEntity);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String responseString = EntityUtils.toString(responseEntity);
        System.out.println(responseString);
    }


}
