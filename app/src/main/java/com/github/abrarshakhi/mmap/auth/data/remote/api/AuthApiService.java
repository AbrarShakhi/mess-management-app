package com.github.abrarshakhi.mmap.auth.data.remote.api;

import com.github.abrarshakhi.mmap.auth.data.remote.dto.LoginRequestDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.LoginResponseDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.SignupRequestDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.SignupResponseDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.VerifyOtpRequestDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.VerifyOtpResponseDto;

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

    @POST("/auth/v1/signup")
    Call<SignupResponseDto> signup(
        @Header("apikey") String apiKey,
        @Body SignupRequestDto request
    );

    @POST("/auth/v1/verify")
    Call<LoginResponseDto> verifyOtp(
        @Header("apikey") String apiKey,
        @Body VerifyOtpRequestDto request
    );
}

