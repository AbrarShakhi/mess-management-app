package com.github.abrarshakhi.mmap.mess.domain.usecase.request;

public class CreateNewMessRequest {
    public String name;
    public String location;
    public String city;
    public String currency;

    public CreateNewMessRequest(String name, String location, String city, String currency) {
        this.name = name;
        this.location = location;
        this.city = city;
        this.currency = currency;
    }
}