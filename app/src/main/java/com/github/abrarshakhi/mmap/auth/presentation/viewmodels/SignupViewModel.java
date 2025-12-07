package com.github.abrarshakhi.mmap.auth.presentation.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.abrarshakhi.mmap.auth.domain.model.SignupRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.SignupResult;
import com.github.abrarshakhi.mmap.auth.domain.model.VerifyOtpRequest;
import com.github.abrarshakhi.mmap.auth.domain.model.VerifyOtpResult;
import com.github.abrarshakhi.mmap.auth.domain.usecase.SignupUseCase;
import com.github.abrarshakhi.mmap.auth.domain.usecase.VerifyOtpUseCase;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignupViewModel extends ViewModel {
    private final ExecutorService executor;

    private final SignupUseCase signupUseCase;
    private final VerifyOtpUseCase verifyOtpUseCase;
    public MutableLiveData<SignupResult> signupResult = new MutableLiveData<>();
    public MutableLiveData<VerifyOtpResult> verifyOtpResult = new MutableLiveData<>();

    public SignupViewModel(SignupUseCase signupUseCase, VerifyOtpUseCase verifyOtpUseCase) {
        this.signupUseCase = signupUseCase;
        this.verifyOtpUseCase = verifyOtpUseCase;
        executor = Executors.newFixedThreadPool(2);
    }

    public void signup(@NotNull String fullName, String phoneNumber, @NotNull String emailAddress, @NotNull String password, @NotNull String retypedPassword) {
        executor.execute(() -> {
            SignupResult result = signupUseCase.execute(new SignupRequest(fullName, phoneNumber, emailAddress, password, retypedPassword));
            signupResult.postValue(result);
        });
    }

    public void verifyOtp(@NotNull String emailAddress, @NotNull String otpToken) {
        executor.execute(() -> {
            var result = verifyOtpUseCase.execute(new VerifyOtpRequest(emailAddress, otpToken));
            verifyOtpResult.postValue(result);
        });
    }
}

