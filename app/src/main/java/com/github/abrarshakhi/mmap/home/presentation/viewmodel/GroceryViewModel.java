package com.github.abrarshakhi.mmap.home.presentation.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.home.domain.model.GroceryBatch;
import com.github.abrarshakhi.mmap.home.domain.usecase.AddGroceryUseCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroceryViewModel extends ViewModel {
    private final ExecutorService executor;
    private final AddGroceryUseCase addGroceryUseCase;

    public MutableLiveData<Outcome<Boolean, String>> addGroceryOutcome = new MutableLiveData<>();

    public GroceryViewModel(AddGroceryUseCase addGroceryUseCase) {
        this.addGroceryUseCase = addGroceryUseCase;
        executor = Executors.newFixedThreadPool(2);
    }

    public void addGrocery(String[] itemNames, float[] prices, String[] quantities) {
        executor.submit(() -> {
            addGroceryOutcome.postValue(addGroceryUseCase.execute(new GroceryBatch(itemNames, prices, quantities)));
        });
    }
}
