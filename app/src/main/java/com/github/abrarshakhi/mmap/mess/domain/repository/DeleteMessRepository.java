package com.github.abrarshakhi.mmap.mess.domain.repository;

import com.github.abrarshakhi.mmap.mess.domain.usecase.result.MessDeleteResult;

public interface DeleteMessRepository {
    MessDeleteResult deleteMess();
}
