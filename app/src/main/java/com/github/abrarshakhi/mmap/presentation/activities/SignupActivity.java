package com.github.abrarshakhi.mmap.presentation.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.data.datasourse.AuthDataSource;
import com.github.abrarshakhi.mmap.data.dto.UserDto;
import com.github.abrarshakhi.mmap.presentation.navigations.SignupNavigation;
import com.github.abrarshakhi.mmap.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private AuthDataSource authDataSource;
    private SignupNavigation navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authDataSource = new AuthDataSource(getApplicationContext());
        navigation = new SignupNavigation(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Go to login
        binding.tvGoToLogin.setOnClickListener(v -> {
            navigation.toLoginActivity();
            finishAffinity();
        });

        // Signup button
        binding.btnSignup.setOnClickListener(v -> {
            binding.pbSignup.setVisibility(View.VISIBLE);
            binding.btnSignup.setVisibility(View.GONE);

            String fullName = binding.etFullNameSignup.getText().toString().trim();
            String phone = binding.etPhoneSignup.getText().toString().trim();
            String email = binding.etEmailSignup.getText().toString().trim();
            String password = binding.etPasswordSignup.getText().toString();
            String confirmPassword = binding.etConfirmPasswordSignup.getText().toString();

            String validationError = validateInputs(fullName, phone, email, password, confirmPassword);
            if (validationError != null) {
                showError(validationError);
                return;
            }

            signup(fullName, phone, email, password);
        });
    }

    private void signup(String fullName, String phone, String email, String password) {
        authDataSource.signup(email, password, authResult -> {
            FirebaseUser user = authResult.getUser();
            if (user == null) {
                showError("Signup failed: no user created");
                return;
            }

            // Send email verification
            authDataSource.sendEmailVerification(user, unused -> {
                Toast.makeText(SignupActivity.this,
                        "Verification email sent to " + email,
                        Toast.LENGTH_LONG).show();

                // Save user profile
                UserDto userDto = new UserDto(fullName, email, phone);
                authDataSource.saveUserProfile(user.getUid(), userDto, unused1 -> {
                    navigation.toLoginActivity();
                    finishAffinity();
                }, e -> {
                    showError("Failed to save user profile: " + e.getMessage());
                });

            }, e -> showError("Failed to send verification email: " + e.getMessage()));

        }, e -> showError("Signup failed: " + e.getMessage()));
    }

    private String validateInputs(String fullName, String phone, String email, String password, String confirmPassword) {
        if (fullName.isEmpty()) return "Full name cannot be empty";
        if (email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) return "Invalid email address";
        if (password.isEmpty()) return "Password cannot be empty";
        if (!password.equals(confirmPassword)) return "Passwords do not match";
        if (!phone.isEmpty() && !phone.matches("^(?:\\+?88)?01[3-9]\\d{8}$")) return "Invalid Bangladeshi phone number format";
        return null;
    }

    private void showError(String message) {
        binding.etErrorStatusSignUp.setText(message);
        binding.etErrorStatusSignUp.setVisibility(View.VISIBLE);
        binding.pbSignup.setVisibility(View.GONE);
        binding.btnSignup.setVisibility(View.VISIBLE);
    }
}
