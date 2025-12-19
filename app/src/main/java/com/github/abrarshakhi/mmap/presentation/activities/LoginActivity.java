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
import com.github.abrarshakhi.mmap.presentation.navigations.LoginNavigation;
import com.github.abrarshakhi.mmap.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthDataSource authDataSource;
    private LoginNavigation navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authDataSource = new AuthDataSource(getApplicationContext());
        navigation = new LoginNavigation(this);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.llLoading.setVisibility(View.VISIBLE);
        binding.svLogin.setVisibility(View.GONE);

        checkLoggedIn();

        binding.tvGoToSignUp.setOnClickListener(v -> {
            navigation.toSignupActivity();
            finishAffinity();
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            navigation.toForgetPasswordActivity();
            finishAffinity();
        });

        binding.btnLogin.setOnClickListener(v -> {
            binding.btnLogin.setVisibility(View.GONE);
            binding.pbLogin.setVisibility(View.VISIBLE);

            String email = binding.editEmail.getText().toString().trim();
            String password = binding.editPassword.getText().toString().trim();

            login(email, password);
        });
    }

    private void checkLoggedIn() {
        FirebaseUser currentUser = authDataSource.getLoggedInUser();
        if (currentUser != null) {
            if (!currentUser.isEmailVerified()) {
                showError("Please verify your email");
                showLoginForm();
                return;
            }

            authDataSource.fetchUserProfile(currentUser.getUid(), userDto -> {
                if (userDto != null) {
                    Toast.makeText(LoginActivity.this,
                            "Logged in as " + userDto.fullName,
                            Toast.LENGTH_SHORT).show();
                    navigation.toHomeActivity();
                    finishAffinity();
                } else {
                    authDataSource.logout();
                    showLoginForm();
                }
            }, e -> {
                showError("Failed to fetch user profile: " + e.getMessage());
                showLoginForm();
            });

        } else {
            showLoginForm();
        }
    }

    private void login(String email, String password) {
        authDataSource.login(email, password, authResult -> {
            FirebaseUser user = authResult.getUser();
            if (user == null) {
                showError("Login failed: no user");
                showLoginForm();
                return;
            }

            if (!user.isEmailVerified()) {
                showError("Please verify your email");
                showLoginForm();
                return;
            }

            // Fetch profile
            authDataSource.fetchUserProfile(user.getUid(), userDto -> {
                if (userDto != null) {
                    Toast.makeText(LoginActivity.this,
                            "Logged in as " + userDto.fullName,
                            Toast.LENGTH_SHORT).show();
                    navigation.toHomeActivity();
                    finishAffinity();
                } else {
                    showError("User profile not found");
                    authDataSource.logout();
                    showLoginForm();
                }
            }, e -> {
                showError("Failed to fetch user profile: " + e.getMessage());
                authDataSource.logout();
                showLoginForm();
            });

        }, e -> {
            showError("Login failed: " + e.getMessage());
            showLoginForm();
        });
    }

    private void showError(String message) {
        binding.etErrorStatus.setText(message);
        binding.etErrorStatus.setVisibility(View.VISIBLE);
        showLoginForm();
    }


    private void showLoginForm() {
        binding.llLoading.setVisibility(View.GONE);
        binding.svLogin.setVisibility(View.VISIBLE);
        binding.btnLogin.setVisibility(View.VISIBLE);
        binding.pbLogin.setVisibility(View.GONE);
    }
}
