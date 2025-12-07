package com.github.abrarshakhi.mmap.auth.data.remote.dto;

import org.jetbrains.annotations.NotNull;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

public class SignupRequestDto {

    @SerializedName("email")
    private final String emailAddress;

    @SerializedName("password")
    private final String password;

    @SerializedName("data")
    private final Map<String, String> data;

    public SignupRequestDto(
        @NotNull String emailAddress,
        @NotNull String password,
        @NotNull String fullName,
        String phone
    ) {
        this.emailAddress = emailAddress;
        this.password = password;
        this.data = new HashMap<>();
        this.data.put("full_name", fullName);
        this.data.put("phone", phone);
    }

    @NotNull
    public String getEmailAddress() {
        return emailAddress;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    @NotNull
    public Map<String, String> getData() {
        return data;
    }
}
