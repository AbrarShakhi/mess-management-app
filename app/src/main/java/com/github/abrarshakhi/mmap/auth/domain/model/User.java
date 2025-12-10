package com.github.abrarshakhi.mmap.auth.domain.model;

public class User {
    private final String uid;
    private final String fullName;
    private final String email;
    private final String phone;

    public User(String uid, String fullName, String email, String phone) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
