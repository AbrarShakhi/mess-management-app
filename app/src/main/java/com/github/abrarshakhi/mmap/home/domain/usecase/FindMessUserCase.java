package com.github.abrarshakhi.mmap.home.domain.usecase;

import com.github.abrarshakhi.mmap.home.domain.repository.FindMessRepository;
import com.github.abrarshakhi.mmap.home.domain.usecase.result.FindMessResult;

public class FindMessUserCase {
    private final FindMessRepository repository;

    public FindMessUserCase(FindMessRepository repo) {
        repository = repo;
    }

    public FindMessResult execute() {
        return repository.findMess();
    }

}