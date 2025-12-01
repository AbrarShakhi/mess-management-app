package com.github.abrarshakhi.mmap.auth.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class LoginErrorResponseDto {
    public int code;

    @SerializedName("error_code")
    public String errorCode;

    public String msg;
}
