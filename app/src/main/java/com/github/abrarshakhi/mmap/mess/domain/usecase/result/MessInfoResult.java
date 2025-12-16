package com.github.abrarshakhi.mmap.mess.domain.usecase.result;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.home.domain.model.Mess;
import com.github.abrarshakhi.mmap.home.domain.model.MessMember;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MessInfoResult {
    private final boolean success;
    private final String errorMessage;
    private final Mess mess;
    private final MessMember messMember;

    private MessInfoResult(boolean success, String message, Mess mess, MessMember messMember) {
        this.success = success;
        this.errorMessage = message;
        this.mess = mess;
        this.messMember = messMember;
    }

    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public static MessInfoResult failure(String errorMessage) {
        return new MessInfoResult(false, errorMessage, null, null);
    }

    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public static MessInfoResult success(@NotNull Mess mess, @NotNull MessMember messMember) {
        return new MessInfoResult(true, null, mess, messMember);
    }

    public Mess getMess() {
        return mess;
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

    public MessMember getMessMember() {
        return messMember;
    }
}