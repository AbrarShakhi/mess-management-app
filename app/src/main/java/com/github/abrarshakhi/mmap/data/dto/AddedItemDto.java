package com.github.abrarshakhi.mmap.data.dto;

public class AddedItemDto {
    public String itemName;
    public float price;
    public String quantity;

    public AddedItemDto() {
    }

    public AddedItemDto(String quantity, float price, String itemName) {
        this.quantity = quantity;
        this.price = price;
        this.itemName = itemName;
    }
}