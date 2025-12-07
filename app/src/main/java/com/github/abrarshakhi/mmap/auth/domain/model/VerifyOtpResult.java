package com.github.abrarshakhi.mmap.auth.domain.model;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class VerifyOtpResult {
    private final boolean success;
    private final String message;

    private VerifyOtpResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public static VerifyOtpResult success(@NotNull String message) {
        return new VerifyOtpResult(true, message);
    }

    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public static VerifyOtpResult failure(@NotNull  String errorMessage) {
        return new VerifyOtpResult(false, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
