package com.github.abrarshakhi.mmap.home.domain.usecase;

import com.github.abrarshakhi.mmap.home.domain.repository.FetchUserRepository;
import com.github.abrarshakhi.mmap.home.domain.usecase.result.UserInfoResult;

public class FetchUserInfoUseCase {
    private final FetchUserRepository repository;

    public FetchUserInfoUseCase(FetchUserRepository repo) {
        repository = repo;
    }

    public UserInfoResult execute() {
        return repository.fetchInfo();
    }
}
