package com.github.abrarshakhi.mmap.auth.data.remote.datasource;

import com.github.abrarshakhi.mmap.BuildConfig;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.LoginRequestDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.SignupRequestDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.SignupResponseDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.TokenResponseDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.VerifyOtpRequestDto;
import com.github.abrarshakhi.mmap.core.connection.ApiModule;
import com.github.abrarshakhi.mmap.core.utils.Outcome;

import java.io.IOException;

import retrofit2.Response;

public class RemoteAuthDataSource extends RemoteRefreshTokenDataSource {

    public RemoteAuthDataSource() {
        super();
    }

    public Outcome<Response<TokenResponseDto>, IOException> login(LoginRequestDto request) {
        var call = apiService.login(BuildConfig.SUPABASE_ANON_KEY, request);
        return Outcome.make(call::execute);
    }

    public Outcome<Response<SignupResponseDto>, IOException> signup(SignupRequestDto request) {
        var call = apiService.signup(BuildConfig.SUPABASE_ANON_KEY, request);
        return Outcome.make(call::execute);
    }

    public Outcome<Response<TokenResponseDto>, IOException> verifyOtp(VerifyOtpRequestDto request) {
        var call = apiService.verifyOtp(BuildConfig.SUPABASE_ANON_KEY, request);
        return Outcome.make(call::execute);
    }
}
