package com.github.abrarshakhi.mmap.auth.data.local.dto;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.auth.data.remote.dto.LoginResponseDto;

import org.jetbrains.annotations.Contract;

public class LoginTokenDto {
    public static final String accessTokenKey = "accessToken";
    public static final String refreshTokenKey = "refreshToken";
    public static final String expiresAtKey = "expiresAt";
    private final String accessToken;
    private final String refreshToken;
    private final Long expiresAt;

    @Contract(pure = true)
    public LoginTokenDto(@NonNull LoginResponseDto data) {
        this.accessToken = data.accessToken;
        this.refreshToken = data.refreshToken;
        this.expiresAt = data.expiresAt;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

}
