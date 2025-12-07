package com.github.abrarshakhi.mmap.auth.domain.model;

public class SignupResult {
    private final boolean success;
    private final String message;

    private SignupResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static SignupResult success(String message) {
        return new SignupResult(true, message);
    }

    public static SignupResult failure(String errorMessage) {
        return new SignupResult(false, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
