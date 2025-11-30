package com.github.abrarshakhi.mmap.core.connection;

import com.github.abrarshakhi.mmap.auth.data.remote.api.AuthApiService;

import org.jetbrains.annotations.NotNull;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkModule {
    private static NetworkModule instance;
    private final Retrofit retrofit;

    private NetworkModule(@NotNull Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public static synchronized NetworkModule getInstance() {
        if (instance == null) {
            String BASE_URL = "https://ojtzllkeupdxdewqedcs.supabase.co";
            Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            instance = new NetworkModule(retrofit);
        }
        return instance;
    }

    public AuthApiService provideAuthApiService() {
        return retrofit.create(AuthApiService.class);
    }
}
