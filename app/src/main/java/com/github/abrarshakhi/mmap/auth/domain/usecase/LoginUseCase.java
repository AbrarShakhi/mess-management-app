package com.github.abrarshakhi.mmap.auth.domain.usecase;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.auth.domain.model.LoginRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.LoginResult;
import com.github.abrarshakhi.mmap.auth.domain.repository.LoginRepository;

public class LoginUseCase {

    private final LoginRepository repository;

    public LoginUseCase(@NonNull LoginRepository repository) {
        this.repository = repository;
    }

    public LoginResult execute(LoginRequest request) {
        if (request.getEmail().isBlank() || request.getPassword().isBlank()) {
            return new LoginResult(LoginResult.CODE.UNSUCCESSFUL, "Empty inputs");
        }
        return repository.login(request);
    }
}