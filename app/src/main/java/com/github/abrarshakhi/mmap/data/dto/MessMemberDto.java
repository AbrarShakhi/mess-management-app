package com.github.abrarshakhi.mmap.data.dto;

public class MessMemberDto {
    public String userId;
    public String role;
    public String messId;
    public long joinedAt;

    public MessMemberDto() {
    }

    public MessMemberDto(String userId, String messId, String role, long joinedAt) {
        this.userId = userId;
        this.messId = messId;
        this.role = role;
        this.joinedAt = joinedAt;
    }
}
