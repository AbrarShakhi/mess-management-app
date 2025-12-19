package com.github.abrarshakhi.mmap.data.mapper;

import com.github.abrarshakhi.mmap.data.dto.GroceryBatchDto;
import com.github.abrarshakhi.mmap.domain.model.GroceryBatch;

public class GroceryMapper {

    public static GroceryBatch toUi(GroceryBatchDto dto, String userName, String currency) {
        GroceryBatch batch = new GroceryBatch();
        batch.batchId = dto.batchId;
        batch.timestamp = dto.timestamp;
        batch.items = dto.items;
        batch.userName = userName;
        batch.currency = currency;
        return batch;
    }
}

