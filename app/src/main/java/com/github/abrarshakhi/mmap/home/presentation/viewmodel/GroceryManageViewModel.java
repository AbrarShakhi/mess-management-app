package com.github.abrarshakhi.mmap.home.presentation.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.home.domain.model.AddedItem;
import com.github.abrarshakhi.mmap.home.domain.model.GroceryBatch;
import com.github.abrarshakhi.mmap.home.domain.usecase.AddGroceryUseCase;
import com.github.abrarshakhi.mmap.home.domain.usecase.AddItemUseCase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroceryManageViewModel extends ViewModel {
    private final ExecutorService executor;
    private final AddGroceryUseCase addGroceryUseCase;
    private final AddItemUseCase addItemUseCase;

    public MutableLiveData<Outcome<Boolean, String>> addGroceryOutcome = new MutableLiveData<>();
    public MutableLiveData<Outcome<AddedItem, String>> addItemOutcome = new MutableLiveData<>();

    public GroceryManageViewModel(AddGroceryUseCase addGroceryUseCase, AddItemUseCase addItemUseCase) {
        this.addGroceryUseCase = addGroceryUseCase;
        this.addItemUseCase = addItemUseCase;
        executor = Executors.newFixedThreadPool(2);
    }

    public void addGrocery(List<AddedItem> items) {
        executor.submit(() -> {
            String[] itemNames = new String[items.size()];
            float[] prices = new float[items.size()];
            String[] quantities = new String[items.size()];
            for (int i = 0; i < items.size(); i++) {
                itemNames[i] = items.get(i).itemName;
                prices[i] = items.get(i).price;
                quantities[i] = items.get(i).quantity;
            }
            addGroceryOutcome.postValue(addGroceryUseCase.execute(new GroceryBatch(itemNames, prices, quantities)));
        });
    }
    public void addItem(AddedItem item) {
        executor.submit(() -> {
            addItemOutcome.postValue(addItemUseCase.execute(item));
        });
    }
}

