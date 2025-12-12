package com.github.abrarshakhi.mmap.home.data.dto;

public class MessMemberDto {
    public String userId;
    public String role;
    public long joinedAt;

    public MessMemberDto() {
    }

    public MessMemberDto(String userId, String role, long joinedAt) {
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
    }
}
