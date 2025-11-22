package com.github.abrarshakhi.mmap.auth.presentation.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.abrarshakhi.mmap.auth.domain.model.SignupRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.SignupResult;
import com.github.abrarshakhi.mmap.auth.domain.usecase.SignupUseCase;

public class SignupViewModel extends ViewModel {

    private final SignupUseCase signupUseCase;
    public MutableLiveData<SignupResult> signupResult = new MutableLiveData<>();

    public SignupViewModel(SignupUseCase signupUseCase) {
        this.signupUseCase = signupUseCase;
    }

    public void signup(String name, String email, String password) {
        SignupResult result = signupUseCase.execute(new SignupRequest(name, email, password));
        signupResult.setValue(result);
    }
}

