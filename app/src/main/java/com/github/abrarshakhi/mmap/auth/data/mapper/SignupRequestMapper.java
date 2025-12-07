package com.github.abrarshakhi.mmap.auth.data.mapper;

import com.github.abrarshakhi.mmap.auth.data.remote.dto.RefreshTokenRequestDto;
import com.github.abrarshakhi.mmap.auth.data.remote.dto.SignupRequestDto;
import com.github.abrarshakhi.mmap.auth.domain.model.SignupRequest;

public class SignupRequestMapper {

    public static SignupRequestDto toDto(SignupRequest signupRequest) {
        if (signupRequest == null) {
            return null;
        }
        return new SignupRequestDto(
            signupRequest.getEmailAddress(),
            signupRequest.getPassword(),
            signupRequest.getFullName(),
            signupRequest.getPhoneNumber());
    }
}
