package com.example.aliez.tomorrowweather.util;
import okhttp3.OkHttpClient;
import okhttp3.Request;
/**
 * Created by Aliez on 2017/3/27.
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
