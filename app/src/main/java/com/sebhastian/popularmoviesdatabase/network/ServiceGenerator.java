package com.sebhastian.popularmoviesdatabase.network;

import com.sebhastian.popularmoviesdatabase.Constants;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Yonathan Sebhastian on 7/22/2017.
 * taken reference from https://futurestud.io/tutorials/retrofit-2-creating-a-sustainable-android-client
 */

public class ServiceGenerator {
    public static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static <S> S createService(Class<S> serviceClass) {
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl url = original.url()
                        .newBuilder()
                        .addQueryParameter(Constants.APP_KEY_QUERY_PARAM, Constants.API_KEY)
                        .build();
                Request request = original.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        });
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}
