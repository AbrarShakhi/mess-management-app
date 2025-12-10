package com.github.abrarshakhi.mmap.auth.presentation.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.abrarshakhi.mmap.auth.domain.usecase.SignupUseCase;
import com.github.abrarshakhi.mmap.auth.domain.usecase.request.SignupRequest;
import com.github.abrarshakhi.mmap.auth.domain.usecase.result.SignupResult;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignupViewModel extends ViewModel {
    private final ExecutorService executor;

    private final SignupUseCase signupUseCase;
    public MutableLiveData<SignupResult> signupResult = new MutableLiveData<>();

    public SignupViewModel(SignupUseCase signupUseCase) {
        this.signupUseCase = signupUseCase;
        executor = Executors.newFixedThreadPool(2);
    }

    public void signup(@NotNull String fullName, String phoneNumber, @NotNull String emailAddress, @NotNull String password, @NotNull String retypedPassword) {
        executor.execute(() -> {
            SignupResult result = signupUseCase.execute(
                new SignupRequest(
                    fullName,
                    phoneNumber,
                    emailAddress,
                    password,
                    retypedPassword
                )
            );
            signupResult.postValue(result);
        });
    }
}

