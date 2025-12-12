package com.github.abrarshakhi.mmap.home.domain.repository;

import com.github.abrarshakhi.mmap.home.domain.usecase.result.UserInfoResult;

public interface FetchUserRepository {
    public UserInfoResult fetchInfo();
}
