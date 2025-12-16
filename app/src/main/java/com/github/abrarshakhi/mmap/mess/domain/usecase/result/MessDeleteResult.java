package com.github.abrarshakhi.mmap.mess.domain.usecase.result;

public class MessDeleteResult {
    public final boolean isSuccess;
    public final String errorMessage;

    public MessDeleteResult(boolean isSuccess, String errorMessage) {
        this.isSuccess = isSuccess;
        this.errorMessage = errorMessage;
    }
}
