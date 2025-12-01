package com.github.abrarshakhi.mmap.auth.domain.model;

public class LoginResult {
    private final int code;
    private final String message;

    public LoginResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() {
        return code == 200;
    }

    public String getMessage() {
        return message;
    }
}
