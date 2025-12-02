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
        return code == CODE.SUCCESSFUL_LOGIN || code == CODE.SUCCESSFUL_LOGGED_IN;
    }

    public String getMessage() {
        return message;
    }

    public enum CODE {
        SUCCESSFUL_LOGIN,
        SUCCESSFUL_LOGGED_IN,
        INVALID_LOGIN_INFO,
        OFFLINE_EXPIRED,
        UNSUCCESSFUL,
        LOGGED_OUT,
    }
}
