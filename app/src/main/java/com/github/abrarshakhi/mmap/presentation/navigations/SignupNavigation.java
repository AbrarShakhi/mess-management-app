package com.github.abrarshakhi.mmap.presentation.navigations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.github.abrarshakhi.mmap.presentation.activities.LoginActivity;

import org.jetbrains.annotations.NotNull;

public class SignupNavigation {
    private final Context context;

    public SignupNavigation(@NotNull Context context) {
        this.context = context;
    }

    public void toLoginActivity() {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    public void toLoginActivity(@NotNull Bundle bundle) {
        context.startActivity(new Intent(context, LoginActivity.class).putExtras(bundle));
    }
}
