package com.github.abrarshakhi.mmap.home.presentation.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.github.abrarshakhi.mmap.home.domain.usecase.FindMessUserCase;
import com.github.abrarshakhi.mmap.home.domain.usecase.result.FindMessResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FindMessViewModel {
    private final ExecutorService executor;
    private final FindMessUserCase findMessUserCase;

    public MutableLiveData<FindMessResult> findMessResult = new MutableLiveData<>();

    public FindMessViewModel(FindMessUserCase findMessUserCase) {
        this.findMessUserCase = findMessUserCase;
        executor = Executors.newFixedThreadPool(2);
    }

    public void findMess() {
        executor.execute(() -> {
            findMessResult.postValue(findMessUserCase.execute());
        });
    }
}