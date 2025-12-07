package com.github.abrarshakhi.mmap.auth.domain.model;

import org.jetbrains.annotations.NotNull;

public class SignupRequest {
    private final String fullName;
    private final String emailAddress;
    private final String password;
    private final String retypedPassword;
    private String phoneNumber;

    public SignupRequest(@NotNull String fullName, @NotNull String emailAddress, @NotNull String password, @NotNull String retypedPassword) {
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.password = password;
        this.retypedPassword = retypedPassword;
    }

    public SignupRequest(@NotNull String fullName, String phoneNumber, @NotNull String emailAddress, @NotNull String password, @NotNull String retypedPassword) {
        this(fullName, emailAddress, password, retypedPassword);
        this.phoneNumber = phoneNumber;
    }

    @NotNull
    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @NotNull
    public String getEmailAddress() {
        return emailAddress;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    @NotNull
    public String getRetypedPassword() {
        return retypedPassword;
    }
}
