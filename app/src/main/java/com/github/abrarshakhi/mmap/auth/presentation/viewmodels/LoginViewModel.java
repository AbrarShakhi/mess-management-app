package com.github.abrarshakhi.mmap.auth.presentation.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.abrarshakhi.mmap.auth.domain.usecase.request.LoginRequest;
import com.github.abrarshakhi.mmap.auth.domain.usecase.result.LoginResult;
import com.github.abrarshakhi.mmap.auth.domain.usecase.CheckLoginUseCase;
import com.github.abrarshakhi.mmap.auth.domain.usecase.LoginUseCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginViewModel extends ViewModel {
    private final LoginUseCase loginUseCase;
    private final CheckLoginUseCase checkLoginUseCase;

    private final ExecutorService executor;
    public MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    public LoginViewModel(LoginUseCase loginUseCase, CheckLoginUseCase checkLoginUseCase) {
        this.loginUseCase = loginUseCase;
        this.checkLoginUseCase = checkLoginUseCase;
        executor = Executors.newFixedThreadPool(2);
    }

    public void isLoggedIn() {
        executor.execute(() -> {
            LoginResult result = checkLoginUseCase.execute();
            loginResult.postValue(result);
        });
    }

    public void login(String email, String password) {
        executor.execute(() -> {
            LoginRequest request = new LoginRequest(email, password);
            LoginResult result = loginUseCase.execute(request);
            loginResult.postValue(result);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}