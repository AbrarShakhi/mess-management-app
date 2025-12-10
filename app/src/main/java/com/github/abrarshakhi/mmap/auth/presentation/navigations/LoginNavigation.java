package com.github.abrarshakhi.mmap.auth.presentation.navigations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.github.abrarshakhi.mmap.auth.presentation.activities.ForgetPasswordActivity;
import com.github.abrarshakhi.mmap.auth.presentation.activities.SignupActivity;
import com.github.abrarshakhi.mmap.home.presentation.activities.HomeActivity;

import org.jetbrains.annotations.NotNull;

public class LoginNavigation {
    private final Context context;

    public LoginNavigation(@NotNull Context context) {
        this.context = context;
    }

    public void toHomeActivity() {
        context.startActivity(new Intent(context, HomeActivity.class));
    }

    public void toHomeActivity(@NotNull Bundle bundle) {
        context.startActivity(new Intent(context, HomeActivity.class).putExtras(bundle));
    }

    public void toSignupActivity() {
        context.startActivity(new Intent(context, SignupActivity.class));
    }

    public void toSignupActivity(@NotNull Bundle bundle) {
        context.startActivity(new Intent(context, SignupActivity.class).putExtras(bundle));
    }

    public void toForgetPasswordActivity() {
        context.startActivity(new Intent(context, ForgetPasswordActivity.class));
    }

    public void toForgetPasswordActivity(@NotNull Bundle bundle) {
        context.startActivity(new Intent(context, ForgetPasswordActivity.class).putExtras(bundle));
    }
}
