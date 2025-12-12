package com.github.abrarshakhi.mmap.home.domain.usecase;

import com.github.abrarshakhi.mmap.home.domain.repository.LogoutRepository;
import com.github.abrarshakhi.mmap.home.domain.usecase.result.LogoutResult;

public class LogoutUseCase {
    private final LogoutRepository repository;
    public LogoutUseCase(LogoutRepository repo) {
        repository = repo;
    }

    public LogoutResult execute() {
        return repository.logout();
    }

}
