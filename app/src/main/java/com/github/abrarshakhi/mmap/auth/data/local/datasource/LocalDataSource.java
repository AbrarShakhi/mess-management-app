package com.github.abrarshakhi.mmap.auth.data.local.datasource;

import android.content.Context;

import com.github.abrarshakhi.mmap.auth.data.local.dto.LoginTokenDto;
import com.github.abrarshakhi.mmap.auth.data.local.storage.AuthStorage;
import com.github.abrarshakhi.mmap.core.utils.Outcome;

import org.jetbrains.annotations.NotNull;

public class LocalDataSource {
    private final AuthStorage storage;

    public LocalDataSource(@NotNull Context context) {
        storage = AuthStorage.newInstance(context).unwrapOr(null);
    }

    public void saveToken(LoginTokenDto token) {
        storage.saveToken(token);
    }

    public Outcome<LoginTokenDto, NullPointerException> loadToken() {
        return storage.loadToken();
    }
}
