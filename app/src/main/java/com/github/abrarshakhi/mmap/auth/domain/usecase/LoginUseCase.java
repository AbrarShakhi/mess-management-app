package com.github.abrarshakhi.mmap.auth.domain.usecase;

import com.github.abrarshakhi.mmap.auth.domain.model.LoginRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.LoginResult;
import com.github.abrarshakhi.mmap.auth.domain.repository.LoginRepository;

public class LoginUseCase {

    private final LoginRepository repository;

    public LoginUseCase(LoginRepository repository) {
        this.repository = repository;
    }

    public LoginResult execute(LoginRequest request) {
        return repository.login(request);
    }
}