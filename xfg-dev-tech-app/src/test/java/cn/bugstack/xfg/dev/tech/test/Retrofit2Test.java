package cn.bugstack.xfg.dev.tech.test;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description okhttp Retrofit2
 * @create 2023-10-14 11:11
 */
public class Retrofit2Test {

    @Test
    public void test_retrofit() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(loggingInterceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.zsxq.com/")
                .client(httpClient.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<String> call = apiService.getTopics("all", 20, "zsxq_access_token=86EB233E-BBD0-1E3C-941D-817CA1303C87_9D76421394C6F474;");

        try {
            String response = call.execute().body();
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    interface ApiService {
        @GET("v2/groups/28885518425541/topics?scope=all&count=20")
        @Headers({
                "Accept-Encoding: deflate, gzip",
                "accept: application/json, text/plain, */*",
                "accept-language: zh-CN,zh;q=0.9,en;q=0.8",
                "authority: api.zsxq.com",
                "dnt: 1",
                "origin: https://wx.zsxq.com",
                "referer: https://wx.zsxq.com/",
                "sec-ch-ua: \"Chromium\";v=\"118\", \"Google Chrome\";v=\"118\", \"Not=A?Brand\";v=\"99\"",
                "sec-ch-ua-mobile: ?0",
                "sec-ch-ua-platform: \"macOS\"",
                "sec-fetch-dest: empty",
                "sec-fetch-mode: cors",
                "sec-fetch-site: same-site",
                "user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36",
                "x-request-id: 372177b46-4e7d-9373-d891-98a22adaeb7",
                "x-signature: 32b39b5d1af5995e3b5022e58a8d8f23cd427434",
                "x-timestamp: 1697249698",
                "x-version: 2.45.0"
        })
        Call<String> getTopics(@Query("scope") String scope, @Query("count") int count, @Header("cookie") String cookie);
    }

}
