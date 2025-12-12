package com.github.abrarshakhi.mmap.home.domain.usecase.result;

public class LogoutResult {
    private final boolean success;
    private final String errorMsg;

    private LogoutResult(boolean success, String errorMsg) {
        this.success = success;
        this.errorMsg = errorMsg;
    }

    public static LogoutResult success() {
        return new LogoutResult(true, "");
    }

    public static LogoutResult failure(String errorMsg) {
        return new LogoutResult(false, errorMsg);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
