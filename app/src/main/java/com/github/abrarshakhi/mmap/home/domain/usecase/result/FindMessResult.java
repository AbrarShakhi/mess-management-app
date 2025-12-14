package com.github.abrarshakhi.mmap.home.domain.usecase.result;

public class FindMessResult {
    private final boolean success;
    private final boolean messFound;
    private final String errorMsg;

    private FindMessResult(boolean success, boolean messFound, String errorMsg) {
        this.success = success;
        this.messFound = messFound;
        this.errorMsg = errorMsg;
    }

    public static FindMessResult success(boolean messFound) {
        return new FindMessResult(true, messFound, "");
    }

    public static FindMessResult failure(String errorMsg) {
        return new FindMessResult(false, false, errorMsg);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public boolean isMessFound() {
        return messFound;
    }
}
