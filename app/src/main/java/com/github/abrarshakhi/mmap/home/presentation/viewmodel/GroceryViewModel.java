package com.github.abrarshakhi.mmap.home.presentation.viewmodel;

import androidx.lifecycle.ViewModel;

import com.github.abrarshakhi.mmap.home.domain.usecase.ListGroceryBatchUseCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroceryViewModel extends ViewModel {
    private final ExecutorService executor;
    private final ListGroceryBatchUseCase listGroceryBatchUseCase;


    public GroceryViewModel(ListGroceryBatchUseCase listGroceryBatchUseCase) {
        this.listGroceryBatchUseCase = listGroceryBatchUseCase;
        executor = Executors.newFixedThreadPool(2);
    }


}
