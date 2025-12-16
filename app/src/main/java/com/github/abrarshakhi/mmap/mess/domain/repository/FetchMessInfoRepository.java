package com.github.abrarshakhi.mmap.mess.domain.repository;

import com.github.abrarshakhi.mmap.mess.domain.usecase.result.MessInfoResult;

public interface FetchMessInfoRepository {
    MessInfoResult fetchMessInfo();
}
