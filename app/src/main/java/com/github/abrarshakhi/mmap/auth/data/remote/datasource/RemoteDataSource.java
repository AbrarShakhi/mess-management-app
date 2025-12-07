package com.github.abrarshakhi.mmap.auth.data.remote.datasource;

import com.github.abrarshakhi.mmap.auth.data.remote.api.AuthApiService;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.LoginRequestDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.LoginResponseDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.SignupRequestDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.SignupResponseDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.VerifyOtpRequestDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.VerifyOtpResponseDto;
import com.github.abrarshakhi.mmap.core.connection.ApiModule;
import com.github.abrarshakhi.mmap.core.utils.Outcome;

import java.io.IOException;

import retrofit2.Response;

public class RemoteDataSource {
    private final AuthApiService apiService;

    public RemoteDataSource() {
        apiService = ApiModule.getInstance().provideAuthApiService();
    }

    public Outcome<Response<LoginResponseDto>, IOException> login(String key, LoginRequestDto request) {
        var call = apiService.login(key, request);
        return Outcome.make(call::execute);
    }

    public Outcome<Response<SignupResponseDto>, IOException> signup(String key, SignupRequestDto request) {
        var call = apiService.signup(key, request);
        return Outcome.make(call::execute);
    }

    public Outcome<Response<LoginResponseDto>, IOException> verifyOtp(String key, VerifyOtpRequestDto request) {
        var call = apiService.verifyOtp(key, request);
        return Outcome.make(call::execute);
    }
}
