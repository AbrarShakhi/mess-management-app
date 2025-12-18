package com.github.abrarshakhi.mmap.home.domain.model;

public  class AddedItem {
    public String itemName;
    public float price;
    public String quantity;

    public AddedItem(String quantity, float price, String itemName) {
        this.quantity = quantity;
        this.price = price;
        this.itemName = itemName;
    }
}