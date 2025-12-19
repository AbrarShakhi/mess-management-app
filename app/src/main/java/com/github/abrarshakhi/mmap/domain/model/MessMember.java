package com.github.abrarshakhi.mmap.domain.model;

public class MessMember {
    private final String userId;
    private final String messId;
    private final String role;
    private final long joinedAt;

    public MessMember(String userId, String messId, String role, long joinedAt) {
        this.userId = userId;
        this.messId = messId;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public long getJoinedAt() {
        return joinedAt;
    }

    public String getMessId() {
        return messId;
    }
}
