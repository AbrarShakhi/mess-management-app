package com.github.abrarshakhi.mmap.auth.domain.repository;

import com.github.abrarshakhi.mmap.auth.domain.usecase.request.LoginRequest;
import com.github.abrarshakhi.mmap.auth.domain.usecase.result.LoginResult;

public interface LoginRepository {
    LoginResult login(LoginRequest request);
    LoginResult isLoggedIn();
}
