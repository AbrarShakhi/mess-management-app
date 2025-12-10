package com.github.abrarshakhi.mmap.auth.domain.repository;

import com.github.abrarshakhi.mmap.auth.domain.usecase.request.SignupRequest;
import com.github.abrarshakhi.mmap.auth.domain.usecase.result.SignupResult;

public interface SignupRepository {
    SignupResult signup(SignupRequest request);
}
