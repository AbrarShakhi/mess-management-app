package com.github.abrarshakhi.mmap.auth.data.local.datasource;

import android.content.Context;

import com.github.abrarshakhi.mmap.auth.data.local.dao.LoginTokenDao;
import com.github.abrarshakhi.mmap.auth.data.local.storage.AuthStorage;
import com.github.abrarshakhi.mmap.core.utils.Outcome;

import org.jetbrains.annotations.NotNull;

public class LocalAuthDataSource {
    protected final AuthStorage storage;

    public LocalAuthDataSource(@NotNull Context context) {
        storage = AuthStorage.newInstance(context).unwrapOr(null);
    }

    public void saveToken(LoginTokenDao token) {
        storage.saveToken(token);
    }

    public Outcome<LoginTokenDao, NullPointerException> loadToken() {
        return storage.loadToken();
    }

    public void clearAll() {
        storage.clearAll();
    }
}
