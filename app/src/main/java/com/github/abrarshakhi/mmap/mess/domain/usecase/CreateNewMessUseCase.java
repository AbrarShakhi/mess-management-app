package com.github.abrarshakhi.mmap.mess.domain.usecase;

import com.github.abrarshakhi.mmap.mess.domain.repository.CreateNewMessRepository;
import com.github.abrarshakhi.mmap.mess.domain.usecase.request.CreateNewMessRequest;
import com.github.abrarshakhi.mmap.mess.domain.usecase.result.CreateNewMessResult;

public class CreateNewMessUseCase {
    public final  CreateNewMessRepository repository;

    public CreateNewMessUseCase(CreateNewMessRepository repository) {
        this.repository = repository;
    }

    public CreateNewMessResult execute(CreateNewMessRequest request) {
        return repository.createMess(request);
    }
}
