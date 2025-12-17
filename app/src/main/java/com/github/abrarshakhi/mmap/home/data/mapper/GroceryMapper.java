package com.github.abrarshakhi.mmap.home.data.mapper;

import com.github.abrarshakhi.mmap.home.data.dto.GroceryDto;
import com.github.abrarshakhi.mmap.home.domain.model.GroceryBatch;

import java.util.*;
import java.util.stream.Collectors;

public final class GroceryMapper {

    private GroceryMapper() {
        // no instance
    }

    /**
     * Converts a GroceryBatch into a list of GroceryDto objects.
     */
    public static List<GroceryDto> toDtoListFromBatch(GroceryBatch batch) {
        if (batch == null) return Collections.emptyList();

        int size = batch.getItemNames().length;
        List<GroceryDto> dtos = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            GroceryDto dto = new GroceryDto();
            String[] ids = batch.getIds();
            dto.groceryId = (ids != null && ids.length > i) ? ids[i] : null;
            dto.messId = batch.getMessId();
            dto.userId = batch.getUserId();
            dto.itemName = batch.getItemNames()[i];
            dto.price = batch.getPrices()[i];
            dto.quantity = batch.getQuantities()[i];
            dto.month = batch.getMonth();
            dto.year = batch.getYear();
            dto.timestamp = batch.getTimestamp();

            dtos.add(dto);
        }

        return dtos;
    }


    /**
     * Groups a list of GroceryDto into GroceryBatch objects based on
     * month, year, messId, userId, and timestamp.
     */
    public static List<GroceryBatch> toDomainGrouped(List<GroceryDto> dtos) {
        if (dtos == null || dtos.isEmpty()) return Collections.emptyList();

        // Use a Map to group by key: month-year-messId-userId-timestamp
        Map<String, List<GroceryDto>> grouped = dtos.stream()
            .collect(Collectors.groupingBy(dto -> dto.messId + "|" + dto.userId + "|" + dto.month + "|" + dto.year + "|" + dto.timestamp));

        List<GroceryBatch> batches = new ArrayList<>();

        for (List<GroceryDto> group : grouped.values()) {
            int size = group.size();
            String[] ids = new String[size];
            String[] itemNames = new String[size];
            float[] prices = new float[size];
            String[] quantities = new String[size];

            for (int i = 0; i < size; i++) {
                GroceryDto dto = group.get(i);
                ids[i] = dto.groceryId;
                itemNames[i] = dto.itemName;
                prices[i] = dto.price;
                quantities[i] = dto.quantity;
            }

            GroceryDto first = group.get(0); // take the common properties from the first
            GroceryBatch batch = new GroceryBatch(
                ids,
                first.messId,
                first.userId,
                itemNames,
                prices,
                quantities,
                first.month,
                first.year,
                first.timestamp
            );

            batches.add(batch);
        }

        return batches;
    }
}
