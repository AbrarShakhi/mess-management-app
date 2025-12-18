package com.github.abrarshakhi.mmap.home.presentation.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.home.domain.model.GroceryBatch;
import com.github.abrarshakhi.mmap.home.domain.model.MonthYear;
import com.github.abrarshakhi.mmap.home.domain.usecase.FindCurrentMonthYearUseCase;
import com.github.abrarshakhi.mmap.home.domain.usecase.ListGroceryBatchUseCase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroceryViewModel extends ViewModel {
    private final ExecutorService executor;
    private final ListGroceryBatchUseCase listGroceryBatchUseCase;
    private final FindCurrentMonthYearUseCase findCurrentMonthYearUseCase;

    public MutableLiveData<Outcome<MonthYear, String>> monthYear = new MutableLiveData<>();
    public MutableLiveData<Outcome<List<GroceryBatch>, String>> listGroceries = new MutableLiveData<>();
    public MutableLiveData<Double> totalPrice = new MutableLiveData<>();

    public GroceryViewModel(ListGroceryBatchUseCase listGroceryBatchUseCase, FindCurrentMonthYearUseCase findCurrentMonthYearUseCase) {
        this.listGroceryBatchUseCase = listGroceryBatchUseCase;
        this.findCurrentMonthYearUseCase = findCurrentMonthYearUseCase;
        executor = Executors.newFixedThreadPool(3);
    }

    public void findMonthYear() {
        executor.submit(() -> {
            monthYear.postValue(findCurrentMonthYearUseCase.execute());
        });
    }

    public void totalGrocery() {
        if (listGroceries.getValue() == null || listGroceries.getValue().hasErr()) {
            return;
        }
        var list = listGroceries.getValue().unwrap();
        executor.submit(() -> {
            var totPrice = 0.0;
            for (var gb: list) {
                for (var price: gb.getPrices()) {
                    totPrice += price;
                    totalPrice.postValue(totPrice);
                }
            }
        });
    }

    public void listGroceries() {
        if (monthYear.getValue() == null || monthYear.getValue().hasErr()) {
            return;
        }
        executor.submit(() -> {
            var list = listGroceryBatchUseCase.execute(monthYear.getValue().unwrap());
            listGroceries.postValue(list);
            totalGrocery();
        });
    }

    public void previousMonth() {
        if (monthYear.getValue() != null && monthYear.getValue().isOK()) {
            monthYear.setValue(MonthYear.validateInstance(monthYear.getValue().unwrap().previous()));
        }
    }

    public void nextMonth() {
        if (monthYear.getValue() != null && monthYear.getValue().isOK()) {
            monthYear.setValue(MonthYear.validateInstance(monthYear.getValue().unwrap().next()));
        }
    }
}
