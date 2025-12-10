package com.github.abrarshakhi.mmap.auth.presentation.navigations;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.github.abrarshakhi.mmap.auth.presentation.activities.LoginActivity;

import org.jetbrains.annotations.NotNull;

public class SignupNavigation {
    private final Activity activity;

    public SignupNavigation(@NotNull Activity activity) {
        this.activity = activity;
    }

    public SignupNavigation toLoginActivity() {
        activity.startActivity(new Intent(activity, LoginActivity.class));
        return this;
    }

    public SignupNavigation toLoginActivity(@NotNull Bundle bundle) {
        activity.startActivity(new Intent(activity, LoginActivity.class).putExtras(bundle));
        return this;
    }

    public void finishAffinity() {
        activity.finishAffinity();
    }
}
