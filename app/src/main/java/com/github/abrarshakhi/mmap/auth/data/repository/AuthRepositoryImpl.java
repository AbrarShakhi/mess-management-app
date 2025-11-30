package com.github.abrarshakhi.mmap.auth.data.repository;

import com.github.abrarshakhi.mmap.auth.data.remote.api.AuthApiService;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.LoginRequestDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.LoginResponseDto;
import com.github.abrarshakhi.mmap.auth.domain.model.LoginRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.LoginResult;
import com.github.abrarshakhi.mmap.auth.domain.model.SignupRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.SignupResult;
import com.github.abrarshakhi.mmap.auth.domain.repository.LoginRepository;
import com.github.abrarshakhi.mmap.auth.domain.repository.SignupRepository;

import retrofit2.Call;
import retrofit2.Response;

public class AuthRepositoryImpl implements LoginRepository, SignupRepository {
    private final AuthApiService api;

    public AuthRepositoryImpl(AuthApiService api) {
        this.api = api;
    }

    @Override
    public LoginResult login(LoginRequest request) {
        try {
            Call<LoginResponseDto> call = api.login(new LoginRequestDto(request.getEmail(), request.getPassword()));
            Response<LoginResponseDto> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                return new LoginResult(response.body().success, response.body().message);
            } else {
                return new LoginResult(false, "Server error");
            }
        } catch (Exception e) {
            return new LoginResult(false, "Network error: " + e.getMessage());
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
