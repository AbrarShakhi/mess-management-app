package com.github.abrarshakhi.mmap.home.domain.usecase;

import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.home.domain.model.AddedItem;

public class AddItemUseCase {
    public Outcome<AddedItem, String> execute(AddedItem item) {
        if (item.itemName.isBlank()) {
            return Outcome.failure("Item Name is blank");
        }
        if (item.quantity.isBlank()) {
            return Outcome.failure("Item Name is blank");
        }
        if (item.price < 1) {
            return Outcome.failure("Price is lower than 1");
        }
        if (!item.quantity.matches("\\d+(\\.\\d+)?[a-zA-Z]+")) {
            return Outcome.failure("Quantity must be like 1kg, 500g, 2pcs (no spaces)");
        }

        return Outcome.success(item);
    }
}
