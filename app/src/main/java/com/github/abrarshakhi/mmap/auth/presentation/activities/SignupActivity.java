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
import com.github.abrarshakhi.mmap.auth.domain.repository.SignupRepository;
import com.github.abrarshakhi.mmap.auth.domain.usecase.SignupUseCase;
import com.github.abrarshakhi.mmap.auth.domain.usecase.VerifyOtpUseCase;
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

        SignupRepository repo = null;

        var signupUseCase = new SignupUseCase(repo);
        var verifyOtpCuseCase = new VerifyOtpUseCase(repo);
        viewModel = new SignupViewModel(signupUseCase, verifyOtpCuseCase);
        navigation = new SignupNavigation(this);

        // Go to login button
        binding.tvGoToLogin.setOnClickListener(v -> {
            navigation.toLoginActivity();
            finishAffinity();
        });

        // Signup button
        binding.btnSignup.setOnClickListener(v -> {
            binding.btnSignup.setEnabled(false);
            viewModel.signup(
                    binding.etFullNameSignup.getText().toString(),
                    binding.etPhoneSignup.getText().toString(),
                    binding.etEmailSignup.getText().toString(),
                    binding.etPasswordSignup.getText().toString(),
                    binding.etConfirmPasswordSignup.getText().toString()
            );
        });
        viewModel.signupResult.observe(this, (result) -> {
            binding.btnSignup.setEnabled(true);
            if (result.isSuccess()) {
                binding.btnSignup.setVisibility(View.GONE);
                binding.llEnterOtp.setVisibility(View.VISIBLE);
                binding.etEmailSignup.setEnabled(false);
            }
            Toast.makeText(SignupActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
        });

        // verify OTP button
        binding.btnSubmitOtpSignup.setOnClickListener(v -> {
            binding.btnSubmitOtpSignup.setEnabled(false);
            viewModel.verifyOtp(
                    binding.etEmailSignup.getText().toString(),
                    binding.etOtpSignup.getText().toString()
            );
        });
        viewModel.verifyOtpResult.observe(this, (result) -> {
            binding.btnSubmitOtpSignup.setEnabled(true);
            if (result.isSuccess()) {
                navigation.toLoginActivity();
                finishAffinity();
            }
            Toast.makeText(SignupActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
