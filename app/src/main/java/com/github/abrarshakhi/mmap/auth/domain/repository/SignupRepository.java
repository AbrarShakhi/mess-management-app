package com.github.abrarshakhi.mmap.auth.domain.repository;

import com.github.abrarshakhi.mmap.auth.domain.model.SignupRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.SignupResult;
import com.github.abrarshakhi.mmap.auth.domain.model.VerifyOtpRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.VerifyOtpResult;

public interface SignupRepository {
    SignupResult signup(SignupRequest request);
    VerifyOtpResult verifyOtp(VerifyOtpRequest request);
}
