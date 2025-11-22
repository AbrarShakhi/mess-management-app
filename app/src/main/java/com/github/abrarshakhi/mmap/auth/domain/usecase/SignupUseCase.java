package com.github.abrarshakhi.mmap.auth.domain.usecase;

import com.github.abrarshakhi.mmap.auth.domain.model.SignupRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.SignupResult;
import com.github.abrarshakhi.mmap.auth.domain.repository.SignupRepository;

public class SignupUseCase {
    private final SignupRepository repository;

    public SignupUseCase(SignupRepository repository) {
        this.repository = repository;
    }

    public SignupResult execute(SignupRequest request) {
        return repository.signup(request);
    }
}
