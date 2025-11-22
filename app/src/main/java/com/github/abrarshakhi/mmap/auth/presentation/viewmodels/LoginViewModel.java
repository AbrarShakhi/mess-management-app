package com.github.abrarshakhi.mmap.auth.presentation.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.abrarshakhi.mmap.auth.domain.model.LoginRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.LoginResult;
import com.github.abrarshakhi.mmap.auth.domain.usecase.LoginUseCase;

public class LoginViewModel extends ViewModel {

    private final LoginUseCase loginUseCase;

    public MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    public LoginViewModel(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    public void login(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        LoginResult result = loginUseCase.execute(request);
        loginResult.setValue(result);
    }
}