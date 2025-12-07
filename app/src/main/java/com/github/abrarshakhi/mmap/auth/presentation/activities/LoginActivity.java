package com.github.abrarshakhi.mmap.auth.presentation.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.auth.data.local.datasource.LocalDataSource;
import com.github.abrarshakhi.mmap.auth.data.local.storage.AuthStorage;
import com.github.abrarshakhi.mmap.auth.data.remote.datasource.RemoteDataSource;
import com.github.abrarshakhi.mmap.auth.data.repository.AuthRepositoryImpl;
import com.github.abrarshakhi.mmap.auth.domain.model.LoginResult;
import com.github.abrarshakhi.mmap.auth.domain.usecase.CheckLoginUseCase;
import com.github.abrarshakhi.mmap.auth.domain.usecase.LoginUseCase;
import com.github.abrarshakhi.mmap.auth.presentation.navigations.LoginNavigation;
import com.github.abrarshakhi.mmap.auth.presentation.viewmodels.LoginViewModel;
import com.github.abrarshakhi.mmap.core.connection.ApiModule;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEt;
    private EditText passEt;
    private Button loginBtn;
    private LinearLayout loginLayout;
    private TextView errorStatus, goToSignUp, forgotPassword;
    private LoginViewModel viewModel;
    private LoginNavigation navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginLayout = findViewById(R.id.llLogin);
        emailEt = findViewById(R.id.editEmail);
        passEt = findViewById(R.id.editPassword);
        loginBtn = findViewById(R.id.btnLogin);
        errorStatus = findViewById(R.id.etErrorStatus);
        goToSignUp = findViewById(R.id.tvGoToSignUp);
        forgotPassword = findViewById(R.id.tvForgotPassword);

        var remoteDataSource = new RemoteDataSource();
        var localDataSource = new LocalDataSource(this);
        var repo = new AuthRepositoryImpl(remoteDataSource, localDataSource);

        var loginUseCase = new LoginUseCase(repo);
        var checkIsLoggedIn = new CheckLoginUseCase(repo);
        viewModel = new LoginViewModel(loginUseCase, checkIsLoggedIn);

        viewModel.isLoggedIn();
        navigation = new LoginNavigation(this);

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
            if (result.isSuccess()
                    || result.getCode() == LoginResult.CODE.OFFLINE_EXPIRED) {
                errorStatus.setVisibility(View.GONE);
                navigation.toHomeActivity();
                finishAffinity();
            } else if (result.getCode() == LoginResult.CODE.INVALID_LOGIN_INFO) {
                errorStatus.setVisibility(View.VISIBLE);
            } else if (result.getCode() != LoginResult.CODE.LOGGED_OUT) {
                Toast.makeText(LoginActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
            }
            loginBtn.setEnabled(true);
            if (loginLayout.getVisibility() != View.VISIBLE) {
                loginLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}
