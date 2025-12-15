package com.github.abrarshakhi.mmap.home.presentation.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.abrarshakhi.mmap.home.domain.usecase.FetchUserInfoUseCase;
import com.github.abrarshakhi.mmap.home.domain.usecase.LogoutUseCase;
import com.github.abrarshakhi.mmap.home.domain.usecase.result.LogoutResult;
import com.github.abrarshakhi.mmap.home.domain.usecase.result.UserInfoResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileViewModel extends ViewModel {
    private final ExecutorService executor;

    private final LogoutUseCase logoutUseCase;
    private final FetchUserInfoUseCase fetchUserInfoUseCase;

    public MutableLiveData<LogoutResult> logoutResult = new MutableLiveData<>();
    public MutableLiveData<UserInfoResult> userInfoResult = new MutableLiveData<>();

    public ProfileViewModel(LogoutUseCase logoutUseCase, FetchUserInfoUseCase fetchUserInfoUseCase) {
        this.logoutUseCase = logoutUseCase;
        this.fetchUserInfoUseCase = fetchUserInfoUseCase;
        executor = Executors.newFixedThreadPool(2);
    }

    public void logout() {
        executor.execute(() -> {
            logoutResult.postValue(logoutUseCase.execute());
        });
    }

    public void fetchInfo() {
        executor.execute(() -> {
            userInfoResult.postValue(fetchUserInfoUseCase.execute());
        });
    }
}
