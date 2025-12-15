package com.github.abrarshakhi.mmap.mess.domain.usecase.result;

public class CreateNewMessResult {
    private final boolean success;
    private final String errorMsg;

    private CreateNewMessResult(boolean success, String errorMsg) {
        this.success = success;
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public static CreateNewMessResult success() {
        return new CreateNewMessResult(true, "");
    }

    public static CreateNewMessResult failure(String ErrMsg) {
        return new CreateNewMessResult(false, ErrMsg);
    }
}
