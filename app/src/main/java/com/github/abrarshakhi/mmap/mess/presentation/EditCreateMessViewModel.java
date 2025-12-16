package com.github.abrarshakhi.mmap.mess.presentation;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.abrarshakhi.mmap.mess.domain.usecase.FetchMessInfoUseCase;
import com.github.abrarshakhi.mmap.mess.domain.usecase.result.MessDeleteResult;
import com.github.abrarshakhi.mmap.mess.domain.usecase.result.MessInfoResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditCreateMessViewModel extends ViewModel {
    private final FetchMessInfoUseCase fetchMessInfoUseCase;
    private final ExecutorService executor;
    public MutableLiveData<MessInfoResult> messInfoResult = new MutableLiveData<>();
    public MutableLiveData<MessDeleteResult> messDeleteResult = new MutableLiveData<>();

    public EditCreateMessViewModel(FetchMessInfoUseCase fetchMessInfoUseCase) {
        this.fetchMessInfoUseCase = fetchMessInfoUseCase;
        executor = Executors.newFixedThreadPool(4);
    }

    public void fetchMessInfo() {
        executor.submit(() -> {
            messInfoResult.postValue(fetchMessInfoUseCase.execute());
        });
    }

    public void listMess() {
        executor.submit(() -> {
        });
    }

    public void deleteMess() {
        executor.submit(() -> {
        });
    }
}
