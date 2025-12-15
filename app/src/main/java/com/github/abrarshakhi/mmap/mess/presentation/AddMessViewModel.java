package com.github.abrarshakhi.mmap.mess.presentation;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.abrarshakhi.mmap.mess.domain.usecase.CreateNewMessUseCase;
import com.github.abrarshakhi.mmap.mess.domain.usecase.request.CreateNewMessRequest;
import com.github.abrarshakhi.mmap.mess.domain.usecase.result.CreateNewMessResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddMessViewModel extends ViewModel {
    private final ExecutorService executor;
    private final CreateNewMessUseCase createNewMessUseCase;
    public MutableLiveData<CreateNewMessResult> createNewMessResult = new MutableLiveData<>();

    public AddMessViewModel(CreateNewMessUseCase createNewMessUseCase) {
        this.createNewMessUseCase = createNewMessUseCase;
        executor = Executors.newFixedThreadPool(2);
    }

    public void createMess(String name, String location, String city, String currency) {
        executor.submit(() -> {
            createNewMessResult.postValue(createNewMessUseCase.execute(new CreateNewMessRequest(name, location, city, currency)));
        });
    }
}
