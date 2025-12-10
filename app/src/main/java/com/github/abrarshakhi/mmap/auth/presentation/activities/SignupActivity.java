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
import com.github.abrarshakhi.mmap.auth.domain.usecase.SignupUseCase;
import com.github.abrarshakhi.mmap.auth.presentation.navigations.SignupNavigation;
import com.github.abrarshakhi.mmap.auth.presentation.viewmodels.SignupViewModel;
import com.github.abrarshakhi.mmap.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {
    SignupViewModel viewModel;
    SignupNavigation navigation;
    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        var datasource = new RemoteDataSource();
        var repo = new AuthRepositoryImpl(datasource);
        var signupUseCase = new SignupUseCase(repo);
        viewModel = new SignupViewModel(signupUseCase);
        navigation = new SignupNavigation(this);

        // Go to login button
        binding.tvGoToLogin.setOnClickListener(v ->
            navigation.toLoginActivity().finishAffinity()
        );

        // Signup button
        binding.btnSignup.setOnClickListener(v -> {
            binding.pbSignup.setVisibility(View.VISIBLE);
            binding.btnSignup.setVisibility(View.GONE);
            viewModel.signup(
                binding.etFullNameSignup.getText().toString(),
                binding.etPhoneSignup.getText().toString(),
                binding.etEmailSignup.getText().toString(),
                binding.etPasswordSignup.getText().toString(),
                binding.etConfirmPasswordSignup.getText().toString()
            );
        });
        viewModel.signupResult.observe(this, (result) -> {
            if (result.isSuccess()) {
                User user = result.getUser();
                if (user != null) {
                    Toast
                        .makeText(SignupActivity.this, "Sent a link to your email: " + user.getFullName(), Toast.LENGTH_SHORT)
                        .show();
                    navigation.toLoginActivity().finishAffinity();
                    return;
                }
            } else {
                binding.etErrorStatusSignUp.setText(result.getErrorMessage());
            }
            binding.pbSignup.setVisibility(View.GONE);
            binding.btnSignup.setVisibility(View.VISIBLE);
        });
    }
}
