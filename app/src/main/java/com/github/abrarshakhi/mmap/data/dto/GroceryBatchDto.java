package com.github.abrarshakhi.mmap.data.dto;

import java.util.List;

public class GroceryBatchDto {
    public String batchId;
    public String messId;
    public String userId;
    public int month;
    public int year;
    public long timestamp;

    public List<AddedItemDto> items;

    public GroceryBatchDto() {}
}

