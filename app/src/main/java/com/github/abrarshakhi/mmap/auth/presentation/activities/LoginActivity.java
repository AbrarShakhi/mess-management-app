package com.github.abrarshakhi.mmap.auth.presentation.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.auth.data.datasourse.RemoteDataSource;
import com.github.abrarshakhi.mmap.auth.data.repository.AuthRepositoryImpl;
import com.github.abrarshakhi.mmap.auth.domain.model.User;
import com.github.abrarshakhi.mmap.auth.domain.usecase.CheckLoginUseCase;
import com.github.abrarshakhi.mmap.auth.domain.usecase.LoginUseCase;
import com.github.abrarshakhi.mmap.auth.presentation.navigations.LoginNavigation;
import com.github.abrarshakhi.mmap.auth.presentation.viewmodels.LoginViewModel;
import com.github.abrarshakhi.mmap.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    private LoginViewModel viewModel;
    private LoginNavigation navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        var dataSource = new RemoteDataSource();
        var repo = new AuthRepositoryImpl(dataSource);
        var loginUseCase = new LoginUseCase(repo);
        var checkIsLoggedIn = new CheckLoginUseCase(repo);
        viewModel = new LoginViewModel(loginUseCase, checkIsLoggedIn);

        navigation = new LoginNavigation(this);

        binding.tvGoToSignUp.setOnClickListener(v ->
            navigation.toSignupActivity().finishAffinity()
        );
        binding.tvForgotPassword.setOnClickListener(v ->
            navigation.toForgetPasswordActivity().finishAffinity()
        );

        binding.btnLogin.setOnClickListener(v -> {
            binding.btnLogin.setVisibility(View.GONE);
            binding.pbLogin.setVisibility(View.VISIBLE);
            viewModel.login(binding.editEmail.getText().toString(), binding.editPassword.getText().toString());
        });

        viewModel.loginResult.observe(this, result -> {
            if (result.isSuccess()) {
                User user = result.getUser();
                if (user != null) {
                    Toast
                        .makeText(LoginActivity.this, "Logged in as " + user.getFullName(), Toast.LENGTH_SHORT)
                        .show();
                    navigation.toHomeActivity().finishAffinity();
                    return;
                }
            } else {
                binding.etErrorStatus.setText(result.getErrorMessage());
            }
            binding.btnLogin.setVisibility(View.VISIBLE);
            binding.pbLogin.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.isLoggedIn();
    }
}
