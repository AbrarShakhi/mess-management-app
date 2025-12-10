package com.github.abrarshakhi.mmap.auth.domain.usecase;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.auth.domain.repository.SignupRepository;
import com.github.abrarshakhi.mmap.auth.domain.usecase.request.SignupRequest;
import com.github.abrarshakhi.mmap.auth.domain.usecase.result.SignupResult;

public class SignupUseCase {
    private final SignupRepository repository;

    public SignupUseCase(@NonNull SignupRepository repository) {
        this.repository = repository;
    }

    public SignupResult execute(@NonNull SignupRequest request) {

        if (isNullOrEmpty(request.getFullName())) {
            return SignupResult.failure("Full name cannot be empty");
        }

        if (isNullOrEmpty(request.getEmail()) || !isValidEmail(request.getEmail())) {
            return SignupResult.failure("Invalid email address");
        }

        if (isNullOrEmpty(request.getPassword())) {
            return SignupResult.failure("Password cannot be empty");
        }

        if (!request.getPassword().equals(request.getRetypedPassword())) {
            return SignupResult.failure("Passwords do not match");
        }

        String phone = request.getPhone();

        if (!isNullOrEmpty(phone)) {
            if (!isValidBangladeshPhoneNumber(phone)) {
                return SignupResult.failure("Invalid Bangladeshi phone number format");
            }
        }

        return repository.signup(request);
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean isValidBangladeshPhoneNumber(String phone) {
        return phone.matches("^(?:\\+?88)?01[3-9]\\d{8}$");
    }
}

