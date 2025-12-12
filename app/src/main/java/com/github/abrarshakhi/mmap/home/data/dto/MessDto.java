package com.github.abrarshakhi.mmap.home.data.dto;

public class MessDto {
    public String messId;
    public String name;
    public String location;
    public String city;
    public int month;
    public String currency;
    public String createdBy;

    public MessDto() {}

    public MessDto(String messId, String name, String location, String city,
                   int month, String currency, String createdBy) {
        this.messId = messId;
        this.name = name;
        this.location = location;
        this.city = city;
        this.month = month;
        this.currency = currency;
        this.createdBy = createdBy;
    }
}
