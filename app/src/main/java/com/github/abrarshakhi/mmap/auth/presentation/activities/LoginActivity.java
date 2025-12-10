package com.github.abrarshakhi.mmap.auth.presentation.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.auth.domain.repository.LoginRepository;
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


        LoginRepository repo = null;

        var loginUseCase = new LoginUseCase(repo);
        var checkIsLoggedIn = new CheckLoginUseCase(repo);
        viewModel = new LoginViewModel(loginUseCase, checkIsLoggedIn);

        navigation = new LoginNavigation(this);

        binding.btnLogin.setOnClickListener(v -> {
            binding.btnLogin.setVisibility(View.GONE);
            binding.pbLogin.setVisibility(View.VISIBLE);
            viewModel.login(
                    binding.editEmail.getText().toString(),
                    binding.editPassword.getText().toString()
            );
        });
        binding.tvGoToSignUp.setOnClickListener(v -> {
            navigation.toSignupActivity();
            finishAffinity();
        });
        binding.tvForgotPassword.setOnClickListener(v -> {
            navigation.toForgetPasswordActivity();
            finishAffinity();
        });

        viewModel.loginResult.observe(this, result -> {
            binding.btnLogin.setVisibility(View.VISIBLE);
            binding.pbLogin.setVisibility(View.GONE);
            //
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
