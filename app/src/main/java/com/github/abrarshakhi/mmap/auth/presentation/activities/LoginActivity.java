package com.github.abrarshakhi.mmap.auth.presentation.activities;

import android.content.Intent;
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
import com.github.abrarshakhi.mmap.auth.data.remote.api.AuthApiService;
import com.github.abrarshakhi.mmap.auth.data.repository.AuthRepositoryImpl;
import com.github.abrarshakhi.mmap.auth.domain.repository.LoginRepository;
import com.github.abrarshakhi.mmap.auth.domain.usecase.LoginUseCase;
import com.github.abrarshakhi.mmap.auth.presentation.viewmodels.LoginViewModel;
import com.github.abrarshakhi.mmap.core.connection.NetworkModule;
import com.github.abrarshakhi.mmap.home.presentation.activities.HomeActivity;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText emailEt;
    EditText passEt;
    Button loginBtn;
    TextView errorStatus, goToSignUp, forgotPassword;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
        initViews();
        initViewModel();
        initListener();
        initObserver();
    }

    private void initLayout() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        emailEt = findViewById(R.id.editEmail);
        passEt = findViewById(R.id.editPassword);
        loginBtn = findViewById(R.id.btnLogin);
        errorStatus = findViewById(R.id.etErrorStatus);
        goToSignUp = findViewById(R.id.tvGoToSignUp);
        forgotPassword = findViewById(R.id.tvForgotPassword);
    }

    private void initViewModel() {
        AuthApiService api = NetworkModule.getInstance().provideAuthApiService();
        LoginRepository repo = new AuthRepositoryImpl(api);
        LoginUseCase loginUseCase = new LoginUseCase(repo);
        viewModel = new LoginViewModel(loginUseCase);
    }

    private void initListener() {
        loginBtn.setOnClickListener(v -> {
            String email = Objects.requireNonNull(emailEt.getText()).toString().trim();
            String pass = Objects.requireNonNull(passEt.getText()).toString().trim();
            viewModel.login(email, pass);
        });
        goToSignUp.setOnClickListener(v -> {
            // TODO: Navigate to signup page
        });
        forgotPassword.setOnClickListener(v -> {
            // TODO: Navigate to forget password page
        });
    }

    private void initObserver() {
        viewModel.loginResult.observe(this, result -> {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            if (result.isSuccess()) {
                errorStatus.setVisibility(View.GONE);
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finishAffinity();
            } else {
                errorStatus.setVisibility(View.VISIBLE);
            }
        });
    }
}
