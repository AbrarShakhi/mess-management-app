package com.github.abrarshakhi.mmap.home.domain.usecase.result;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.auth.domain.model.User;

import org.jetbrains.annotations.Contract;

public class UserInfoResult {
    private final boolean success;
    private final String errorMessage;
    private final User user;

    private UserInfoResult(boolean success, String message, User user) {
        this.success = success;
        this.errorMessage = message;
        this.user = user;
    }

    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public static UserInfoResult failure(String errorMessage) {
        return new UserInfoResult(false, errorMessage, null);
    }

    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public static UserInfoResult success(User user) {
        return new UserInfoResult(true, null, user);
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
