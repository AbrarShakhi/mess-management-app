package com.github.abrarshakhi.mmap.auth.domain.usecase.request;


public class SignupRequest {

    private final String fullName;
    private final String phone;
    private final String email;
    private final String password;
    private final String retypedPassword;

    public SignupRequest(String fullName, String phone, String email, String password, String retypedPassword) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.retypedPassword = retypedPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRetypedPassword() {
        return retypedPassword;
    }
}
