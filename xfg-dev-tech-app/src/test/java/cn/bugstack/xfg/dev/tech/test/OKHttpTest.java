package cn.bugstack.xfg.dev.tech.test;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description OKHttp
 * @create 2023-10-14 11:26
 */
public class OKHttpTest {

    @Test
    public void test_okhttp() throws Exception {
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.zsxq.com/v2/groups/28885518425541/topics?scope=all&count=20";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept-Encoding", "deflate, gzip")
                .addHeader("accept", "application/json, text/plain, */*")
                .addHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8")
                .addHeader("authority", "api.zsxq.com")
                .addHeader("cookie", "__cuid=5330a556392a4c5b8084b4cbc165e0f3; zsxq_access_token=86EB233E-BBD0-1E3C-941D-817CA1303C87_9D76421394C6F474; abtest_env=product; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%22241858242255511%22%2C%22first_id%22%3A%2217ebd0b4317ecb-0b27f672c2d3af-133a6253-1296000-17ebd0b4318ba7%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%2C%22%24latest_referrer%22%3A%22%22%7D%2C%22%24device_id%22%3A%2217ebd0b4317ecb-0b27f672c2d3af-133a6253-1296000-17ebd0b4318ba7%22%2C%22identities%22%3A%22eyIkaWRlbnRpdHlfY29va2llX2lkIjoiMTgwMmQ2YjZiOWIxZjMtMGQ4YzMzZjhmYTA3YmEtMzU3MzZhMDMtMTI5NjAwMC0xODAyZDZiNmI5YzEwODYiLCIkaWRlbnRpdHlfbG9naW5faWQiOiIyNDE4NTgyNDIyNTU1MTEifQ%3D%3D%22%2C%22history_login_id%22%3A%7B%22name%22%3A%22%24identity_login_id%22%2C%22value%22%3A%22241858242255511%22%7D%7D; zsxqsessionid=4ac8449313681ca8b436d052eab5925e")
                .addHeader("dnt", "1")
                .addHeader("origin", "https://wx.zsxq.com")
                .addHeader("referer", "https://wx.zsxq.com/")
                .addHeader("sec-ch-ua", "\"Chromium\";v=\"118\", \"Google Chrome\";v=\"118\", \"Not=A?Brand\";v=\"99\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"macOS\"")
                .addHeader("sec-fetch-dest", "empty")
                .addHeader("sec-fetch-mode", "cors")
                .addHeader("sec-fetch-site", "same-site")
                .addHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36")
                .addHeader("x-request-id", "372177b46-4e7d-9373-d891-98a22adaeb7")
                .addHeader("x-signature", "32b39b5d1af5995e3b5022e58a8d8f23cd427434")
                .addHeader("x-timestamp", "1697249698")
                .addHeader("x-version", "2.45.0")
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                String string = responseBody.string();
                System.out.println(string);
            }
        });

        // 等待回调
        new CountDownLatch(1).await();

    }

}
