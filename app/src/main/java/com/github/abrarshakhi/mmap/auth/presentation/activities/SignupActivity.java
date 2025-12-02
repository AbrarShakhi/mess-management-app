package com.github.abrarshakhi.mmap.auth.presentation.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.auth.data.local.storage.AuthStorage;
import com.github.abrarshakhi.mmap.auth.data.repository.AuthRepositoryImpl;
import com.github.abrarshakhi.mmap.auth.domain.usecase.SignupUseCase;
import com.github.abrarshakhi.mmap.auth.presentation.navigations.SignupNavigation;
import com.github.abrarshakhi.mmap.auth.presentation.viewmodels.SignupViewModel;
import com.github.abrarshakhi.mmap.core.connection.ApiModule;

public class SignupActivity extends AppCompatActivity {
    EditText etFullNameSignup, etPhoneSignup, etEmailSignup, etPasswordSignup,
            etConfirmPasswordSignup, etOtpSignup, btnSendOtpSignup;
    Button btnSignup, btnSubmitOtpSignup;
    TextView tvGoToLogin;
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
        etPhoneSignup = findViewById(R.id.etPhoneSignp);
        etEmailSignup = findViewById(R.id.etEmailSignup);
        etPasswordSignup = findViewById(R.id.etPasswordSignup);
        etConfirmPasswordSignup = findViewById(R.id.etConfirmPasswordSignup);
        btnSignup = findViewById(R.id.btnSignup);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        llEnterOtp = findViewById(R.id.llEnterOtp);
        etOtpSignup = findViewById(R.id.etOtpSignup);
        btnSendOtpSignup = findViewById(R.id.btnSendOtpSignup);
        btnSubmitOtpSignup = findViewById(R.id.btnSubmitOtpSignup);

        var api = ApiModule.getInstance().provideAuthApiService();
        var local = AuthStorage.getInstance(this).unwrapOr(null);
        var repo = new AuthRepositoryImpl(api, local);

        var signupUseCase = new SignupUseCase(repo);
        viewModel = new SignupViewModel(signupUseCase);
        navigation = new SignupNavigation(this);

        tvGoToLogin.setOnClickListener(v -> {
            navigation.toLoginActivity();
            finishAffinity();
        });
    }
}