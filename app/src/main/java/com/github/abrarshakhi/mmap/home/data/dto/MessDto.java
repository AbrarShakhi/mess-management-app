package com.github.abrarshakhi.mmap.home.data.dto;

import java.util.List;

public class MessDto {
    public String messId;
    public String name;
    public String location;
    public String city;
    public int month;
    public int year;
    public String currency;
    public String createdBy;
    public List<MessMemberDto> members;

    public MessDto() {
    }

    public MessDto(String messId, String name, String location, String city,
                   int month, int year, String currency, String createdBy,
                   List<MessMemberDto> members) { // include members in constructor
        this.messId = messId;
        this.name = name;
        this.location = location;
        this.city = city;
        this.month = month;
        this.year = year;
        this.currency = currency;
        this.createdBy = createdBy;
        this.members = members;
    }
}
