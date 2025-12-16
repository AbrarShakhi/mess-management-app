package com.github.abrarshakhi.mmap.mess.domain.usecase;

import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.home.domain.model.Mess;
import com.github.abrarshakhi.mmap.mess.domain.repository.SelectMessRepository;

public class SelectMessUseCase {
    private final SelectMessRepository repository;

    public SelectMessUseCase(SelectMessRepository repository) {
        this.repository = repository;
    }

    public Outcome<Boolean, String> execute(Mess mess) {
        return repository.selectMess(mess.getMessId());
    }
}
