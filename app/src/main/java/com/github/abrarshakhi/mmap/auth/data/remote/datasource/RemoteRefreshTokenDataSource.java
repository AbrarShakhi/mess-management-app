package com.github.abrarshakhi.mmap.auth.data.remote.datasource;

import com.github.abrarshakhi.mmap.BuildConfig;
import com.github.abrarshakhi.mmap.auth.data.remote.api.AuthApiService;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.RefreshTokenRequestDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.TokenResponseDto;
import com.github.abrarshakhi.mmap.core.connection.ApiModule;
import com.github.abrarshakhi.mmap.core.utils.Outcome;

import java.io.IOException;

import retrofit2.Response;

public class RemoteRefreshTokenDataSource {
    protected final AuthApiService apiService;

    public RemoteRefreshTokenDataSource() {
        this.apiService = ApiModule.getInstance().provideAuthApiService();
    }

    public Outcome<Response<TokenResponseDto>, IOException> refreshToken(RefreshTokenRequestDto request) {
        var call = apiService.refreshToken(BuildConfig.SUPABASE_ANON_KEY, request);
        return Outcome.make(call::execute);
    }
}
