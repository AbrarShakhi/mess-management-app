package com.github.abrarshakhi.mmap.domain.model;

import java.io.Serializable;

public class MessMember implements Serializable {
    public final String fullName;
    public String userId;
    public String role;
    public String messId;
    public long joinedAt;
    public float houseRent;
    public float utility;

    public MessMember(String userId, String messId, String role, long joinedAt, float houseRent, float utility, String fullName) {
        this.userId = userId;
        this.messId = messId;
        this.role = role;
        this.joinedAt = joinedAt;
        this.houseRent = houseRent;
        this.utility = utility;
        this.fullName = fullName;
    }

}
