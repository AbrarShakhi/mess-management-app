package com.github.abrarshakhi.mmap.mess.domain.repository;

import com.github.abrarshakhi.mmap.mess.domain.usecase.request.CreateNewMessRequest;
import com.github.abrarshakhi.mmap.mess.domain.usecase.result.CreateNewMessResult;

public interface CreateNewMessRepository {
    CreateNewMessResult createMess(CreateNewMessRequest request);
}
