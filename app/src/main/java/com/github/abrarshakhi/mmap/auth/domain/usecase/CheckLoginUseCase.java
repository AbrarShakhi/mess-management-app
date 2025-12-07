package com.github.abrarshakhi.mmap.auth.domain.usecase;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.auth.domain.model.LoginResult;
import com.github.abrarshakhi.mmap.auth.domain.repository.LoginRepository;

public class CheckLoginUseCase {
    private final LoginRepository repository;

    public CheckLoginUseCase(@NonNull LoginRepository repository) {
        this.repository = repository;
    }

    public LoginResult execute() {
        return repository.isLoggedIn();
    }
}

