package com.github.abrarshakhi.mmap.home.data.dto;

public class GroceryDto {
    public String groceryId;
    public String messId;
    public String userId;

    public String itemName;
    public float price;
    public String quantity;

    public int month; // 0 - 11
    public int year;
    public long timestamp;

    public GroceryDto() {}

    public GroceryDto(String userId, String itemName, float price, String quantity, int month, int year, long timestamp) {
        this.userId = userId;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.month = month;
        this.year = year;
        this.timestamp = timestamp;
    }
}

