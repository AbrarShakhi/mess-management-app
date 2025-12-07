package com.github.abrarshakhi.mmap.auth.data.local.dao;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.auth.data.remote.dto.TokenResponseDto;

import org.jetbrains.annotations.Contract;

public class LoginTokenDao {
    public static final String accessTokenKey = "accessToken";
    public static final String refreshTokenKey = "refreshToken";
    public static final String expiresAtKey = "expiresAt";
    public static final String idKey = "id";
    public static final String emailKey = "email";
    private final String accessToken;
    private final String refreshToken;
    private final Long expiresAt;
    private final String userId;
    private final String email;

    @Contract(pure = true)
    public LoginTokenDao(@NonNull TokenResponseDto data) {
        this.accessToken = data.accessToken;
        this.refreshToken = data.refreshToken;
        this.expiresAt = data.expiresAt;
        this.userId = data.user.id;
        this.email = data.user.email;
    }
    public LoginTokenDao(String accessToken, String refreshToken, Long expiresAt, String userId, String email) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.userId = userId;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
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
