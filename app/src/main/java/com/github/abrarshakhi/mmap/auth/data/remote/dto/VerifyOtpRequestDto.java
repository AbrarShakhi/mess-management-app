package com.github.abrarshakhi.mmap.auth.data.remote.dto;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class VerifyOtpRequestDto {

    @SerializedName("email")
    private final String email;

    @SerializedName("token")
    private final String otp;

    @SerializedName("type")
    private final String type;

    public VerifyOtpRequestDto(String email, String otp, String type) {
        this.email = email;
        this.otp = otp;
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public String getOtp() {
        return otp;
    }

    public String getType() {
        return type;
    }
}

