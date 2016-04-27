package com.repository.Utils;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by rich on 2016/4/18.
 */
public class HttpClientUtil {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String POST = "POST";
    public static final String GET = "GET";

    private static OkHttpClient client = new OkHttpClient();

    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
