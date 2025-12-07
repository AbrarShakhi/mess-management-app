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
import com.github.abrarshakhi.mmap.auth.data.remote.datasource.RemoteDataSource;
import com.github.abrarshakhi.mmap.auth.data.repository.AuthRepositoryImpl;
import com.github.abrarshakhi.mmap.auth.domain.usecase.SignupUseCase;
import com.github.abrarshakhi.mmap.auth.domain.usecase.VerifyOtpUseCase;
import com.github.abrarshakhi.mmap.auth.presentation.navigations.SignupNavigation;
import com.github.abrarshakhi.mmap.auth.presentation.viewmodels.SignupViewModel;

public class SignupActivity extends AppCompatActivity {
    EditText etFullNameSignup, etPhoneSignup, etEmailSignup, etPasswordSignup,
        etConfirmPasswordSignup, etOtpSignup;
    Button btnSignup, btnSubmitOtpSignup;
    TextView tvGoToLogin, btnSendOtpSignup;
    LinearLayout llEnterOtp;

    SignupViewModel viewModel;
    SignupNavigation navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etFullNameSignup = findViewById(R.id.etFullNameSignup);
        etPhoneSignup = findViewById(R.id.etPhoneSignup);
        etEmailSignup = findViewById(R.id.etEmailSignup);
        etPasswordSignup = findViewById(R.id.etPasswordSignup);
        etConfirmPasswordSignup = findViewById(R.id.etConfirmPasswordSignup);
        btnSignup = findViewById(R.id.btnSignup);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        llEnterOtp = findViewById(R.id.llEnterOtp);
        etOtpSignup = findViewById(R.id.etOtpSignup);
        btnSendOtpSignup = findViewById(R.id.btnSendOtpSignup);
        btnSubmitOtpSignup = findViewById(R.id.btnSubmitOtpSignup);

        var remoteDataSource = new RemoteDataSource();
        var localDataSource = new LocalDataSource(this);
        var repo = new AuthRepositoryImpl(remoteDataSource, localDataSource);

        var signupUseCase = new SignupUseCase(repo);
        var verifyOtpCuseCase = new VerifyOtpUseCase(repo);
        viewModel = new SignupViewModel(signupUseCase, verifyOtpCuseCase);
        navigation = new SignupNavigation(this);

        // Go to login button
        tvGoToLogin.setOnClickListener(v -> {
            navigation.toLoginActivity();
            finishAffinity();
        });

        // Signup button
        btnSignup.setOnClickListener(v -> {
            btnSignup.setEnabled(false);
            viewModel.signup(
                etFullNameSignup.getText().toString(),
                etPhoneSignup.getText().toString(),
                etEmailSignup.getText().toString(),
                etPasswordSignup.getText().toString(),
                etConfirmPasswordSignup.getText().toString()
            );
        });
        viewModel.signupResult.observe(this, (result) -> {
            btnSignup.setEnabled(true);
            if (result.isSuccess()) {
                btnSignup.setVisibility(View.GONE);
                llEnterOtp.setVisibility(View.VISIBLE);
                etEmailSignup.setEnabled(false);
            }
            Toast.makeText(SignupActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
        });

        // verify OTP button
        btnSubmitOtpSignup.setOnClickListener(v -> {
            btnSubmitOtpSignup.setEnabled(false);
            viewModel.verifyOtp(
                etEmailSignup.getText().toString(),
                etOtpSignup.getText().toString()
            );
        });
        viewModel.verifyOtpResult.observe(this, (result) -> {
            btnSubmitOtpSignup.setEnabled(true);
            if (result.isSuccess()) {
                navigation.toLoginActivity();
                finishAffinity();
            }
            Toast.makeText(SignupActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
