package com.github.abrarshakhi.mmap.home.domain.usecase;

import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.home.domain.model.GroceryBatch;
import com.github.abrarshakhi.mmap.home.domain.repository.GroceryRepository;

public class AddGroceryUseCase {
    public final GroceryRepository repository;

    public AddGroceryUseCase(GroceryRepository repository) {
        this.repository = repository;
    }

    public Outcome<Boolean, String> execute(GroceryBatch groceryBatch) {
        var items = groceryBatch.getItemNames();
        var prices = groceryBatch.getPrices();
        var quantities = groceryBatch.getQuantities();
        if (items.length != prices.length && items.length != quantities.length) {
            return Outcome.failure("number of items length did not match with quantity, price");
        }

        for (var itm : items) {
            if (itm.isBlank()) {
                return Outcome.failure("Item Name can not be empty");
            }
        }
        for (var price : prices) {
            if (price < 0) {
                return Outcome.failure("Price uis negative?");
            }
        }
        for (var quantity : quantities) {
            if (quantity.isBlank()) {
                return Outcome.failure("Price uis negative?");
            }
            if (!quantity.matches("\\d+(\\.\\d+)?[a-zA-Z]+")) {
                return Outcome.failure("Quantity must be like 1kg, 500g, 2pcs (no spaces)");
            }
        }
        return repository.addGrocery(groceryBatch);
    }
}
