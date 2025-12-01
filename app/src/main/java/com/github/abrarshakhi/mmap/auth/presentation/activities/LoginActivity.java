package com.github.abrarshakhi.mmap.auth.presentation.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.auth.data.local.storage.AuthStorage;
import com.github.abrarshakhi.mmap.auth.data.remote.api.AuthApiService;
import com.github.abrarshakhi.mmap.auth.data.repository.AuthRepositoryImpl;
import com.github.abrarshakhi.mmap.auth.domain.model.LoginResult;
import com.github.abrarshakhi.mmap.auth.domain.repository.LoginRepository;
import com.github.abrarshakhi.mmap.auth.domain.usecase.CheckLoginUseCase;
import com.github.abrarshakhi.mmap.auth.domain.usecase.LoginUseCase;
import com.github.abrarshakhi.mmap.auth.presentation.navigations.LoginNavigation;
import com.github.abrarshakhi.mmap.auth.presentation.viewmodels.LoginViewModel;
import com.github.abrarshakhi.mmap.core.connection.ApiModule;
import com.github.abrarshakhi.mmap.core.utils.Outcome;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEt;
    private EditText passEt;
    private Button loginBtn;
    private TextView errorStatus, goToSignUp, forgotPassword;
    private LoginViewModel viewModel;
    private LoginNavigation navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AuthApiService api = ApiModule.getInstance().provideAuthApiService();
        Outcome<AuthStorage, Throwable> localStorage = AuthStorage.getInstance(this);
        LoginRepository repo = new AuthRepositoryImpl(api, localStorage.unwrapOr(null));
        LoginUseCase loginUseCase = new LoginUseCase(repo);
        CheckLoginUseCase checkLoginUseCase = new CheckLoginUseCase(repo);
        viewModel = new LoginViewModel(loginUseCase, checkLoginUseCase);

        viewModel.isLoggedIn();
        navigation = new LoginNavigation(this);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEt = findViewById(R.id.editEmail);
        passEt = findViewById(R.id.editPassword);
        loginBtn = findViewById(R.id.btnLogin);
        errorStatus = findViewById(R.id.etErrorStatus);
        goToSignUp = findViewById(R.id.tvGoToSignUp);
        forgotPassword = findViewById(R.id.tvForgotPassword);

        loginBtn.setOnClickListener(v -> {
            loginBtn.setEnabled(false);
            viewModel.login(
                    emailEt.getText().toString(),
                    passEt.getText().toString()
            );
        });
        goToSignUp.setOnClickListener(v -> {
            navigation.toSignupActivity();
            finishAffinity();
        });
        forgotPassword.setOnClickListener(v -> {
            navigation.toForgetPasswordActivity();
            finishAffinity();
        });

        viewModel.loginResult.observe(this, result -> {
            loginBtn.setEnabled(true);
            if (result.isSuccess()) {
                errorStatus.setVisibility(View.GONE);
                navigation.toHomeActivity();
                finishAffinity();
            } else if (result.getCode() == LoginResult.CODE.LOGGED_IN) {
                navigation.toHomeActivity();
                finishAffinity();
            } else if (result.getCode() == LoginResult.CODE.INVALID) {
                errorStatus.setVisibility(View.VISIBLE);
            }
        });
    }
}
