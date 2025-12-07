package com.github.abrarshakhi.mmap.auth.data.repository;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.auth.data.local.dao.LoginTokenDao;
import com.github.abrarshakhi.mmap.auth.data.local.datasource.LocalAuthDataSource;
import com.github.abrarshakhi.mmap.auth.data.mapper.SignupRequestMapper;
import com.github.abrarshakhi.mmap.auth.data.remote.datasource.RemoteAuthDataSource;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.ErrorResponseDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.LoginRequestDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.RefreshTokenRequestDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.SignupResponseDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.VerifyOtpRequestDto;
import com.github.abrarshakhi.mmap.auth.data.utils.AuthUtils;
import com.github.abrarshakhi.mmap.auth.domain.model.LoginRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.LoginResult;
import com.github.abrarshakhi.mmap.auth.domain.model.SignupRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.SignupResult;
import com.github.abrarshakhi.mmap.auth.domain.model.VerifyOtpRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.VerifyOtpResult;
import com.github.abrarshakhi.mmap.auth.domain.repository.LoginRepository;
import com.github.abrarshakhi.mmap.auth.domain.repository.SignupRepository;
import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;


public class AuthRepositoryImpl implements LoginRepository, SignupRepository {
    private final RemoteAuthDataSource remoteDs;
    private final LocalAuthDataSource localDs;

    public AuthRepositoryImpl(RemoteAuthDataSource remoteDs, LocalAuthDataSource localDs) {
        this.remoteDs = remoteDs;
        this.localDs = localDs;
    }

    @Override
    public LoginResult login(@NotNull LoginRequest request) {
        var outcome = remoteDs.login(
            new LoginRequestDto(request.getEmail(), request.getPassword())
        );
        if (outcome.hasErr()) {
            return new LoginResult(LoginResult.CODE.UNSUCCESSFUL, "Network Error");
        }
        var response = outcome.unwrap();
        if (response.isSuccessful() && response.body() != null) {
            var token = new LoginTokenDao(response.body());
            localDs.saveToken(token);
            return new LoginResult(LoginResult.CODE.SUCCESSFUL_LOGIN, "Login successfully");
        }
        var errBody = response.errorBody();
        if (errBody == null) {
            return new LoginResult(LoginResult.CODE.INVALID_LOGIN_INFO, "Network Error");
        }
        Outcome<ErrorResponseDto, IOException> errorOutcome = Outcome.make(() -> new Gson()
            .fromJson(errBody.string(), ErrorResponseDto.class));
        errBody.close();
        if (errorOutcome.isOK() && !errorOutcome.isValueNull()) {
            var error = errorOutcome.unwrap();
            if (error.msg != null) {
                return new LoginResult(LoginResult.CODE.INVALID_LOGIN_INFO, Objects.requireNonNullElse(error.msg, "Something went wrong"));
            } else {
                return new LoginResult(LoginResult.CODE.INVALID_LOGIN_INFO, Objects.requireNonNullElse(error.message, "Something went wrong"));
            }
        } else {
            return new LoginResult(LoginResult.CODE.INVALID_LOGIN_INFO, "Something went wrong");
        }
    }

    @Override
    public LoginResult isLoggedIn() {
        var outcome = localDs.loadToken();
        if (outcome.hasErr()) {
            return new LoginResult(LoginResult.CODE.LOGGED_OUT, "Token does not exists.");
        }
        LoginTokenDao tokenDao = outcome.unwrap();
        if (AuthUtils.isTokenValid(tokenDao.getExpiresAt())) {
            return new LoginResult(LoginResult.CODE.SUCCESSFUL_LOGGED_IN, "Login successfully");
        }
        var refreshOutcome = remoteDs.refreshToken(new RefreshTokenRequestDto(tokenDao.getRefreshToken()));
        if (refreshOutcome.hasErr()) {
            return new LoginResult(LoginResult.CODE.OFFLINE_EXPIRED, "Login successfully");
        }
        var response = refreshOutcome.unwrap();
        if (response.isSuccessful() && response.body() != null) {
            var token = new LoginTokenDao(response.body());
            localDs.saveToken(token);
            return new LoginResult(LoginResult.CODE.SUCCESSFUL_LOGIN, "Login successfully");
        }
        localDs.clearAll();
        return new LoginResult(LoginResult.CODE.LOGGED_OUT, "Refresh token expired");
    }

    @Override
    public SignupResult signup(@NonNull SignupRequest request) {
        var outcome = remoteDs.signup(SignupRequestMapper.toDto(request));
        if (outcome.hasErr()) {
            return SignupResult.failure("Network Error");
        }
        var response = outcome.unwrap();
        if (response.isSuccessful() && response.body() != null) {
            SignupResponseDto result = response.body();
            if (result.email != null) {
                return SignupResult.success("We have sent an email to the " + result.email);
            } else {
                return SignupResult.failure("Unable to set otp to the email");
            }
        }
        var errBody = response.errorBody();
        if (errBody == null) {
            return SignupResult.failure("Network Error");
        }
        Outcome<ErrorResponseDto, IOException> errorOutcome = Outcome.make(() -> new Gson()
            .fromJson(errBody.string(), ErrorResponseDto.class));

        errBody.close();
        if (errorOutcome.isOK() && !errorOutcome.isValueNull()) {
            var error = errorOutcome.unwrap();
            if (error.msg != null) {
                return SignupResult.failure(Objects.requireNonNullElse(error.msg, "Something went wrong"));
            } else {
                return SignupResult.failure(Objects.requireNonNullElse(error.message, "Something went wrong"));
            }
        } else {
            return SignupResult.failure("Something went wrong");
        }
    }

    @Override
    public VerifyOtpResult verifyOtp(@NonNull VerifyOtpRequest request) {
        var outcome = remoteDs.verifyOtp(new VerifyOtpRequestDto(
            request.getEmail(),
            request.getOtp(),
            "signup"
        ));
        if (outcome.hasErr()) {
            return VerifyOtpResult.failure("Network Error");
        }
        var response = outcome.unwrap();
        if (response.isSuccessful() && response.body() != null) {
            var token = new LoginTokenDao(response.body());
            localDs.saveToken(token);
            return VerifyOtpResult.success("Signup Successful");
        }
        var errBody = response.errorBody();
        if (errBody == null) {
            return VerifyOtpResult.failure("Network Error");
        }
        Outcome<ErrorResponseDto, IOException> errorOutcome = Outcome.make(() -> new Gson()
            .fromJson(errBody.string(), ErrorResponseDto.class));
        errBody.close();
        if (errorOutcome.isOK() && !errorOutcome.isValueNull()) {
            var error = errorOutcome.unwrap();
            if (error.msg != null) {
                return VerifyOtpResult.failure(Objects.requireNonNullElse(error.msg, "Something went wrong"));
            } else {
                return VerifyOtpResult.failure(Objects.requireNonNullElse(error.message, "Something went wrong"));
            }
        } else {
            return VerifyOtpResult.failure("Something went wrong");
        }
    }
}
