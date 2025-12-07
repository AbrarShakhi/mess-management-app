package com.github.abrarshakhi.mmap.auth.domain.model;

import org.jetbrains.annotations.NotNull;

public class VerifyOtpRequest {
    @NotNull
    private final String email;
    @NotNull
    private final String otp;

    public VerifyOtpRequest(@NotNull String email, @NotNull String otp) {
        this.email = email;
        this.otp = otp;
    }

    public @NotNull String getEmail() {
        return email;
    }

    public @NotNull String getOtp() {
        return otp;
    }
}
