package com.github.abrarshakhi.mmap.auth.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ErrorResponseDto {
    @SerializedName("code")
    public Integer code;

    @SerializedName("error_code")
    public String errorCode;

    @SerializedName("msg")
    public String msg;

    @SerializedName("message")
    public String message;
}
