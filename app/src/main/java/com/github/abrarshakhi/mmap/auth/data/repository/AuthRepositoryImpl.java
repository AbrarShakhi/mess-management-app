package com.github.abrarshakhi.mmap.auth.data.repository;

import com.github.abrarshakhi.mmap.auth.domain.model.LoginRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.LoginResult;
import com.github.abrarshakhi.mmap.auth.domain.model.SignupRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.SignupResult;
import com.github.abrarshakhi.mmap.auth.domain.repository.*;

public class AuthRepositoryImpl implements LoginRepository, SignupRepository {

    @Override
    public LoginResult login(LoginRequest request) {

        // TODO: Replace this with API or Room logic
        if (request.getEmail().equals("test@example.com") &&
            request.getPassword().equals("123456")) {

            return new LoginResult(true, "Login successful");
        }

        return new LoginResult(false, "Invalid credentials");
    }

    @Override
    public SignupResult signup(SignupRequest request) {

        // Fake signup - replace with API
        if (request.getEmail().equals("test@example.com")) {
            return new SignupResult(false, "Email already taken");
        }

        return new SignupResult(true, "Account created successfully");
    }
}
