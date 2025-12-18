package com.github.abrarshakhi.mmap.home.domain.repository;

import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.home.domain.model.GroceryBatch;
import com.github.abrarshakhi.mmap.home.domain.model.MonthYear;

import java.util.List;

public interface GroceryRepository {
    Outcome<Boolean, String> addGrocery(GroceryBatch groceryBatch);
    Outcome<MonthYear, String> findCurrentMonthYear();
    Outcome<List<GroceryBatch>, String>listGrocery(MonthYear monthYear);
}
