package com.github.abrarshakhi.mmap.auth.domain.repository;

import com.github.abrarshakhi.mmap.auth.domain.model.LoginRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.LoginResult;

public interface LoginRepository {
    LoginResult login(LoginRequest request);
    LoginResult isLoggedIn();
}
