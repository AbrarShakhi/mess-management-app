package com.github.abrarshakhi.mmap.auth.domain.usecase;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.auth.domain.repository.LoginRepository;
import com.github.abrarshakhi.mmap.auth.domain.usecase.request.LoginRequest;
import com.github.abrarshakhi.mmap.auth.domain.usecase.result.LoginResult;

public class LoginUseCase {

    private final LoginRepository repository;

    public LoginUseCase(@NonNull LoginRepository repository) {
        this.repository = repository;
    }

    public LoginResult execute(LoginRequest request) {
        if (request.getEmail().isBlank() || request.getPassword().isBlank()) {
            return LoginResult.failure("Empty inputs");
        }
        return repository.login(request);
    }
}