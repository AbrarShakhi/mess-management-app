package com.github.abrarshakhi.mmap.home.domain.model;

public class Mess {
    private final String messId;
    private final String name;
    private final String location;
    private final String city;
    private final int month;
    private final String currency;
    private final String createdBy;

    public Mess(String messId, String name, String location, String city,
                int month, String currency, String createdBy) {
        this.messId = messId;
        this.name = name;
        this.location = location;
        this.city = city;
        this.month = month;
        this.currency = currency;
        this.createdBy = createdBy;
    }

    public String getMessId() {
        return messId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getCity() {
        return city;
    }

    public int getMonth() {
        return month;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
