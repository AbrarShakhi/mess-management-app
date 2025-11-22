package com.github.abrarshakhi.mmap.auth.domain.repository;

import com.github.abrarshakhi.mmap.auth.domain.model.SignupRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.SignupResult;

public interface SignupRepository {
    SignupResult signup(SignupRequest request);
}
