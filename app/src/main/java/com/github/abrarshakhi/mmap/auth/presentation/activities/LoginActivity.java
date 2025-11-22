package com.github.abrarshakhi.mmap.auth.presentation.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.auth.data.repository.AuthRepositoryImpl;
import com.github.abrarshakhi.mmap.auth.domain.repository.LoginRepository;
import com.github.abrarshakhi.mmap.auth.domain.usecase.LoginUseCase;
import com.github.abrarshakhi.mmap.auth.presentation.viewmodels.LoginViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login),
            (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

        TextInputEditText emailEt = findViewById(R.id.editEmail);
        TextInputEditText passEt = findViewById(R.id.editPassword);
        MaterialButton loginBtn = findViewById(R.id.btnLogin);

        LoginRepository repo = new AuthRepositoryImpl();
        LoginUseCase loginUseCase = new LoginUseCase(repo);

        viewModel = new LoginViewModel(loginUseCase);

        loginBtn.setOnClickListener(v -> {
            String email = Objects.requireNonNull(emailEt.getText()).toString();
            String pass = Objects.requireNonNull(passEt.getText()).toString();
            viewModel.login(email, pass);
        });

        viewModel.loginResult.observe(this, result -> {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            if (result.isSuccess()) {

            }
        });
    }
}