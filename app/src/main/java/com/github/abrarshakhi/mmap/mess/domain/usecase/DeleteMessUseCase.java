package com.github.abrarshakhi.mmap.mess.domain.usecase;

import com.github.abrarshakhi.mmap.mess.domain.repository.DeleteMessRepository;
import com.github.abrarshakhi.mmap.mess.domain.usecase.result.MessDeleteResult;

public class DeleteMessUseCase {
    private final DeleteMessRepository repository;

    public DeleteMessUseCase(DeleteMessRepository repository) {
        this.repository = repository;
    }

    public MessDeleteResult execute() {
        return repository.deleteMess();
    }
}
