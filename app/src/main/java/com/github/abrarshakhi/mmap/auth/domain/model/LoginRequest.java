package com.github.abrarshakhi.mmap.auth.domain.model;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email.strip();
        this.password = password.strip();
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
