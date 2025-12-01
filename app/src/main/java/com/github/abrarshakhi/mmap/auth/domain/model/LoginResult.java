package com.github.abrarshakhi.mmap.auth.domain.model;

public class LoginResult {
    private final CODE code;
    private final String message;

    public LoginResult(CODE code, String message) {
        this.code = code;
        this.message = message;
    }

    public CODE getCode() {
        return code;
    }

    public boolean isSuccess() {
        return code == CODE.SUCCESS;
    }

    public String getMessage() {
        return message;
    }

    public enum CODE {
        SUCCESS, NET_ERROR, INVALID, LOGGED_IN, LOGGED_OUT
    }
}
