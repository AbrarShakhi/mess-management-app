package com.github.abrarshakhi.mmap.mess.domain.repository;

import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.home.domain.model.Mess;

import java.util.List;

public interface FetchMessListRepository {
    Outcome<List<Mess>, String> fetchMessList();
}
