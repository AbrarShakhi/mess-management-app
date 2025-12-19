package com.github.abrarshakhi.mmap.domain.model;

import com.github.abrarshakhi.mmap.data.dto.AddedItemDto;

import java.util.List;

public class GroceryBatch {

    public String batchId;
    public String userName;
    public String currency;
    public long timestamp;
    public List<AddedItemDto> items;

    public GroceryBatch() {}

    public float getTotalPrice() {
        float total = 0f;
        if (items != null) {
            for (AddedItemDto item : items) {
                total += item.price;
            }
        }
        return total;
    }
}
