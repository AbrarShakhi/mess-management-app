package com.github.abrarshakhi.mmap.data.dto;

public class MessMemberDto {
    public String userId;
    public String role;
    public String messId;
    public long joinedAt;
    public float houseRent;
    public float utility;

    public MessMemberDto() {
    }

    public MessMemberDto(String userId, String messId, String role, long joinedAt, float houseRent, float utility) {
        this.userId = userId;
        this.messId = messId;
        this.role = role;
        this.joinedAt = joinedAt;
        this.houseRent= houseRent;
        this.utility= utility;
    }
}
