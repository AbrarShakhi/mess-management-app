package com.github.abrarshakhi.mmap.auth.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenRequestDto {
    @SerializedName("refresh_token")
    public String refreshToken;

    public RefreshTokenRequestDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}


