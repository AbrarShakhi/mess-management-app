package com.github.abrarshakhi.mmap.auth.data.repository;

import com.github.abrarshakhi.mmap.BuildConfig;
import com.github.abrarshakhi.mmap.auth.data.local.dto.LoginTokenDto;
import com.github.abrarshakhi.mmap.auth.data.local.storage.AuthStorage;
import com.github.abrarshakhi.mmap.auth.data.remote.api.AuthApiService;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.LoginErrorResponseDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.LoginRequestDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.LoginResponseDto;
import com.github.abrarshakhi.mmap.auth.domain.model.LoginRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.LoginResult;
import com.github.abrarshakhi.mmap.auth.domain.model.SignupRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.SignupResult;
import com.github.abrarshakhi.mmap.auth.domain.repository.LoginRepository;
import com.github.abrarshakhi.mmap.auth.domain.repository.SignupRepository;
import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


public class AuthRepositoryImpl implements LoginRepository, SignupRepository {
    private final AuthApiService api;
    private final AuthStorage authStorage;

    public AuthRepositoryImpl(AuthApiService api, AuthStorage authStorage) {
        this.api = api;
        this.authStorage = authStorage;
    }

    @Override
    public LoginResult login(@NotNull LoginRequest request) {
        Call<LoginResponseDto> call = api.login(
            BuildConfig.SUPABASE_ANON_KEY,
            new LoginRequestDto(request.getEmail(), request.getPassword())
        );
        Outcome<Response<LoginResponseDto>, IOException> outcome = Outcome.make(call::execute);
        if (outcome.hasErr()) {
            return new LoginResult(-1, "Network Error");
        }
        Response<LoginResponseDto> response = outcome.unwrap();
        if (response.isSuccessful() && response.body() != null) {
            LoginTokenDto token = new LoginTokenDto(response.body());
            authStorage.saveToken(token);
            return new LoginResult(response.code(), "Login successfully");
        }
        ResponseBody errBody = response.errorBody();
        if (errBody == null) {
            return new LoginResult(response.code(), "Network Error");
        }
        Outcome<LoginErrorResponseDto, IOException> errorOutcome = Outcome.make(() -> new Gson()
            .fromJson(errBody.string(), LoginErrorResponseDto.class));
        errBody.close();
        if (errorOutcome.isOK()) {
            return new LoginResult(response.code(), errorOutcome.unwrap().msg);
        } else {
            return new LoginResult(response.code(), "Something Went Wrong");
        }
    }

    @Override
    public SignupResult signup(SignupRequest request) {
        if (request.getEmail().equals("test@example.com")) {
            return new SignupResult(false, "Email already taken");
        }

        return new SignupResult(true, "Account created successfully");
    }
}
