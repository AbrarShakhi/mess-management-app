package com.github.abrarshakhi.mmap.auth.domain.usecase.result;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.auth.domain.model.User;

import org.jetbrains.annotations.Contract;

public class LoginResult {
    private final boolean success;
    private final String errorMessage;
    private final User user;

    private LoginResult(boolean success, String message, User user) {
        this.success = success;
        this.errorMessage = message;
        this.user = user;
    }

    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public static LoginResult failure(String errorMessage) {
        return new LoginResult(false, errorMessage, null);
    }

    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public static LoginResult success(User user) {
        return new LoginResult(true, null, user);
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
