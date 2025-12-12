package com.github.abrarshakhi.mmap.home.domain.model;

public class MessMember {
    private final String userId;
    private final String role;
    private final long joinedAt;

    public MessMember(String userId, String role, long joinedAt) {
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public String getUserId() { return userId; }
    public String getRole() { return role; }
    public long getJoinedAt() { return joinedAt; }
}
