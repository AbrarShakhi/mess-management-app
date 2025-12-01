package com.github.abrarshakhi.mmap.auth.domain.usecase;

import com.github.abrarshakhi.mmap.auth.domain.model.LoginResult;
import com.github.abrarshakhi.mmap.auth.domain.repository.LoginRepository;

public class CheckLoginUseCase {
    private final LoginRepository repository;

    public CheckLoginUseCase(LoginRepository repository) {
        this.repository = repository;
    }

    public LoginResult execute() {
        return repository.isLoggedIn();
    }
}

