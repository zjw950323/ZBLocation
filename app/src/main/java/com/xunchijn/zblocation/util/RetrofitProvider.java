package com.xunchijn.zblocation.util;

import android.support.annotation.NonNull;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.xunchijn.zblocation.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2018/5/4 0004.
 */

public class RetrofitProvider {
    //发布
    //  public static final String BASE_URL = "";
    //预发布
    private static final String PRE_RELEASE = "";
    //开发
    //   public static final String BASE_URL = "";
    public static final String BASE_URL = "http://musez.fun:8088/";

    @NonNull
    public static Retrofit get() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        //读取超时
        builder.readTimeout(10, TimeUnit.SECONDS);
        //连接超时
        builder.connectTimeout(9, TimeUnit.SECONDS);

        //DeBug模式下
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }

        //选择使用的网络传输版本

        return new Retrofit.Builder().baseUrl(BASE_URL)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
