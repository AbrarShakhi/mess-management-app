package com.github.abrarshakhi.mmap.auth.data.dto;

public class UserDto {
    public String fullName;
    public String email;
    public String phone;

    public UserDto() {
    }

    public UserDto(String fullName, String email, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }
}