package com.github.abrarshakhi.mmap.mess.domain.usecase;

import com.github.abrarshakhi.mmap.mess.domain.repository.FetchMessInfoRepository;
import com.github.abrarshakhi.mmap.mess.domain.usecase.result.MessInfoResult;

public class FetchMessInfoUseCase {
    private final FetchMessInfoRepository repository;

    public FetchMessInfoUseCase(FetchMessInfoRepository repository) {
        this.repository = repository;
    }

    public MessInfoResult execute() {
        return repository.fetchMessInfo();
    }
}
