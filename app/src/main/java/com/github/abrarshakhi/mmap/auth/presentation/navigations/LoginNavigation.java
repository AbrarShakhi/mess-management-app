package com.github.abrarshakhi.mmap.auth.presentation.navigations;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.github.abrarshakhi.mmap.auth.presentation.activities.ForgetPasswordActivity;
import com.github.abrarshakhi.mmap.auth.presentation.activities.SignupActivity;
import com.github.abrarshakhi.mmap.home.presentation.activities.HomeActivity;

import org.jetbrains.annotations.NotNull;

public class LoginNavigation {
    private final Activity activity;

    public LoginNavigation(@NotNull Activity activity) {
        this.activity = activity;
    }

    public LoginNavigation toHomeActivity() {
        activity.startActivity(new Intent(activity, HomeActivity.class));
        return this;
    }

    public LoginNavigation toHomeActivity(@NotNull Bundle bundle) {
        activity.startActivity(new Intent(activity, HomeActivity.class).putExtras(bundle));
        return this;
    }

    public LoginNavigation toSignupActivity() {
        activity.startActivity(new Intent(activity, SignupActivity.class));
        return this;
    }

    public LoginNavigation toSignupActivity(@NotNull Bundle bundle) {
        activity.startActivity(new Intent(activity, SignupActivity.class).putExtras(bundle));
        return this;
    }

    public LoginNavigation toForgetPasswordActivity() {
        activity.startActivity(new Intent(activity, ForgetPasswordActivity.class));
        return this;
    }

    public LoginNavigation toForgetPasswordActivity(@NotNull Bundle bundle) {
        activity.startActivity(new Intent(activity, ForgetPasswordActivity.class).putExtras(bundle));
        return this;
    }

    public void finishAffinity() {
        activity.finishAffinity();
    }
}
