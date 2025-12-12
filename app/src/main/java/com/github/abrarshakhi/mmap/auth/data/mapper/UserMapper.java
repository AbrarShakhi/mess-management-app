package com.github.abrarshakhi.mmap.auth.data.mapper;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.auth.data.dto.UserDto;
import com.github.abrarshakhi.mmap.auth.domain.model.User;

import org.jetbrains.annotations.Contract;

public class UserMapper {

    @NonNull
    @Contract(value = "_, _ -> new", pure = true)
    public static User dtoToDomain(String uid, @NonNull UserDto dto) {
        return new User(uid, dto.fullName, dto.email, dto.phone);
    }

    @NonNull
    @Contract("_ -> new")
    public static UserDto domainToDto(@NonNull User domainUser) {
        return new UserDto(domainUser.getFullName(), domainUser.getEmail(), domainUser.getPhone());
    }
}