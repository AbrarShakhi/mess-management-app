package com.github.abrarshakhi.mmap.mess.presentation;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.home.domain.model.Mess;
import com.github.abrarshakhi.mmap.mess.domain.usecase.DeleteMessUseCase;
import com.github.abrarshakhi.mmap.mess.domain.usecase.FetchMessInfoUseCase;
import com.github.abrarshakhi.mmap.mess.domain.usecase.ListMessesUseCase;
import com.github.abrarshakhi.mmap.mess.domain.usecase.SelectMessUseCase;
import com.github.abrarshakhi.mmap.mess.domain.usecase.result.MessDeleteResult;
import com.github.abrarshakhi.mmap.mess.domain.usecase.result.MessInfoResult;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditCreateMessViewModel extends ViewModel {
    private final FetchMessInfoUseCase fetchMessInfoUseCase;
    private final DeleteMessUseCase deleteMessUseCase;
    private final ListMessesUseCase listMessesUseCase;
    private final SelectMessUseCase selectMessUseCase;

    private final ExecutorService executor;
    public MutableLiveData<MessInfoResult> messInfoResult = new MutableLiveData<>();
    public MutableLiveData<MessDeleteResult> messDeleteResult = new MutableLiveData<>();
    public MutableLiveData<Outcome<List<Mess>, String>> listMess = new MutableLiveData<>();
    public MutableLiveData<Outcome<Boolean, String>> messSelection = new MutableLiveData<>();

    public EditCreateMessViewModel(FetchMessInfoUseCase fetchMessInfoUseCase, DeleteMessUseCase deleteMessUseCase, ListMessesUseCase listMessesUseCase, SelectMessUseCase selectMessUseCase) {
        this.fetchMessInfoUseCase = fetchMessInfoUseCase;
        this.deleteMessUseCase = deleteMessUseCase;
        this.listMessesUseCase = listMessesUseCase;
        this.selectMessUseCase = selectMessUseCase;
        executor = Executors.newFixedThreadPool(4);
    }

    public void fetchMessInfo() {
        executor.submit(() -> {
            messInfoResult.postValue(fetchMessInfoUseCase.execute());
        });
    }

    public void listMess() {
        executor.submit(() -> {
            var messListOutcome = listMessesUseCase.execute();
            listMess.postValue(messListOutcome);
        });
    }

    public void deleteMess() {
        executor.submit(() -> {
            messDeleteResult.postValue(deleteMessUseCase.execute());
        });
    }

    public void selectMess(Mess mess) {
        executor.submit(() -> {
            messSelection.postValue(
                selectMessUseCase.execute(mess)
            );
        });
    }
}
