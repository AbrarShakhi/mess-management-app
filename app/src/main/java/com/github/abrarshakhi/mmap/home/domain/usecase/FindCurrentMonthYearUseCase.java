package com.github.abrarshakhi.mmap.home.domain.usecase;

import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.home.domain.model.MonthYear;
import com.github.abrarshakhi.mmap.home.domain.repository.GroceryRepository;

public class FindCurrentMonthYearUseCase {
    public final GroceryRepository repository;

    public FindCurrentMonthYearUseCase(GroceryRepository repository) {
        this.repository = repository;
    }

    public Outcome<MonthYear, String> execute() {
        return repository.findCurrentMonthYear();
    }
}
