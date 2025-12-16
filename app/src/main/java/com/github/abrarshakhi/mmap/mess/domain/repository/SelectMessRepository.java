package com.github.abrarshakhi.mmap.mess.domain.repository;

import com.github.abrarshakhi.mmap.core.utils.Outcome;

public interface SelectMessRepository {
    Outcome<Boolean, String> selectMess(String messId);
}
