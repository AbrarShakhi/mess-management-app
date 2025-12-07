package com.github.abrarshakhi.mmap.auth.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class SignupResponseDto {

    @SerializedName("id")
    public String id;

    @SerializedName("aud")
    public String aud;

    @SerializedName("role")
    public String role;

    @SerializedName("email")
    public String email;

    @SerializedName("phone")
    public String phone;

    @SerializedName("confirmation_sent_at")
    public String confirmationSentAt;
}
