package com.github.abrarshakhi.mmap.auth.domain.usecase.result;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.auth.domain.model.User;

import org.jetbrains.annotations.Contract;

public class SignupResult {
    private final boolean success;
    private final String errorMessage;
    private final User user;

    private SignupResult(boolean success, String message, User user) {
        this.success = success;
        this.errorMessage = message;
        this.user = user;
    }

    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public static SignupResult failure(String errorMessage) {
        return new SignupResult(false, errorMessage, null);
    }

    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public static SignupResult success(User user) {
        return new SignupResult(true, null, user);
    }

    public User getUser() {
        return user;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
