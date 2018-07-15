package org.kodluyoruz.milliyet_watchface.api.service;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static String apiBaseUrl = "http://api.sonraneoldu.com/";

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl( apiBaseUrl )
            .addConverterFactory( GsonConverterFactory.create() );

    private static HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor().setLevel( HttpLoggingInterceptor.Level.BODY );

    private static OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

    private ServiceGenerator() {
    }

    public static void changeApiBaseUrl(String newApiBaseUrl) {
        apiBaseUrl = newApiBaseUrl;

        builder.baseUrl( apiBaseUrl );
    }

    public static <S> S createService(Class<S> serviceClass) {

        if (!httpClientBuilder.interceptors().contains( httpLoggingInterceptor )) {
            httpClientBuilder.addInterceptor( httpLoggingInterceptor );
            builder = builder.client( httpClientBuilder.build() );
        }

        Retrofit retrofit = builder.build();
        return retrofit.create( serviceClass );
    }
}
