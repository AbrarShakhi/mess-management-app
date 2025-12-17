package com.github.abrarshakhi.mmap.home.domain.repository;

import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.home.domain.model.GroceryBatch;

public interface GroceryRepository {
    Outcome<Boolean, String> addGrocery(GroceryBatch groceryBatch);
}
