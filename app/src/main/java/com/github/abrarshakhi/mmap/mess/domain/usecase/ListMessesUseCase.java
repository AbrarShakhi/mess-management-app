package com.github.abrarshakhi.mmap.mess.domain.usecase;

import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.home.domain.model.Mess;
import com.github.abrarshakhi.mmap.mess.domain.repository.FetchMessListRepository;

import java.util.List;

public class ListMessesUseCase {
    private final FetchMessListRepository repository;

    public ListMessesUseCase(FetchMessListRepository repository) {
        this.repository = repository;
    }

    public Outcome<List<Mess>, String> execute() {
        return repository.fetchMessList();
    }
}
