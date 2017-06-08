package com.papermelody.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by HgS_1217_ on 2017/5/1.
 */

public class RetrofitClient {
    /**
     * retrofit用于网络通信的client，提供SocialSystemAPI
     */

    // url改为当前寝室网络的ip地址，详见server的文档
    private static String BASE_URL = App.getServerIP();

    private static SocialSystemAPI socialSystemAPI;

    public static SocialSystemAPI getSocialSystemAPI() {
        if (socialSystemAPI == null) {
            Executor executor = Executors.newCachedThreadPool();
            final Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDateTypeAdapter()).create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .callbackExecutor(executor)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(OkHttpProvider.getInstance())
                    .build();

            socialSystemAPI = retrofit.create(SocialSystemAPI.class);
        }
        return socialSystemAPI;
    }

    public static void updateBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
        Executor executor = Executors.newCachedThreadPool();
        final Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDateTypeAdapter()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .callbackExecutor(executor)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(OkHttpProvider.getInstance())
                .build();

        socialSystemAPI = retrofit.create(SocialSystemAPI.class);
    }
}
