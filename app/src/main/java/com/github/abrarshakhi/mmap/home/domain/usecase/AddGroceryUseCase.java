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
        return repository.addGrocery(groceryBatch);
    }
}
