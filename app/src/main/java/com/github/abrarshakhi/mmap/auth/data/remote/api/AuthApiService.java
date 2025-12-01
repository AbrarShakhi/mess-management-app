package com.github.abrarshakhi.mmap.auth.data.remote.api;

import com.github.abrarshakhi.mmap.auth.data.remote.dto.LoginRequestDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.LoginResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("/auth/v1/token?grant_type=password")
    Call<LoginResponseDto> login(
        @Header("apikey") String apiKey,
        @Body LoginRequestDto request
    );
}

