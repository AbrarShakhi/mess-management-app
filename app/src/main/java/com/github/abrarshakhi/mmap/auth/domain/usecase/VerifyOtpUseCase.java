package com.github.abrarshakhi.mmap.auth.domain.usecase;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.auth.domain.model.VerifyOtpRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.VerifyOtpResult;
import com.github.abrarshakhi.mmap.auth.domain.repository.SignupRepository;

public class VerifyOtpUseCase {
    private final SignupRepository repository;

    public VerifyOtpUseCase(@NonNull SignupRepository repository) {
        this.repository = repository;
    }

    public VerifyOtpResult execute(@NonNull VerifyOtpRequest request) {
        if (isNullOrEmpty(request.getEmail()) || isNullOrEmpty(request.getOtp())) {
            return VerifyOtpResult.failure("Inputs are empty");
        }
        return repository.verifyOtp(request);
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
