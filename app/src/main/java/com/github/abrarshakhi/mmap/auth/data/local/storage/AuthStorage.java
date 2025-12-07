package com.github.abrarshakhi.mmap.auth.data.local.storage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.github.abrarshakhi.mmap.auth.data.local.dao.LoginTokenDao;
import com.github.abrarshakhi.mmap.core.storage.LocalPreferences;
import com.github.abrarshakhi.mmap.core.utils.Outcome;

import org.jetbrains.annotations.NotNull;

public class AuthStorage extends LocalPreferences {
    private static final String FILE_NAME = "login_credentials";

    private AuthStorage(SharedPreferences sharedPreferences) {
        super(sharedPreferences);
    }

    public static synchronized Outcome<AuthStorage, Throwable> newInstance(@NotNull Context context) {
        Outcome<SharedPreferences, Throwable> outcome = Outcome.make(() -> {
            MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

            return EncryptedSharedPreferences.create(
                context,
                FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        });
        if (outcome.hasErr()) {
            return Outcome.err(outcome.unwrapErr());
        }
        AuthStorage instance = new AuthStorage(outcome.unwrap());
        return Outcome.ok(instance);
    }

    public void saveToken(LoginTokenDao token) {
        saveString(LoginTokenDao.accessTokenKey, token.getAccessToken());
        saveString(LoginTokenDao.refreshTokenKey, token.getRefreshToken());
        saveLong(LoginTokenDao.expiresAtKey, token.getExpiresAt());
        saveString(LoginTokenDao.emailKey, token.getEmail());
        saveString(LoginTokenDao.idKey, token.getUserId());
    }

    public Outcome<LoginTokenDao, NullPointerException> loadToken() {
        if (!contains(LoginTokenDao.accessTokenKey)
            || !contains(LoginTokenDao.refreshTokenKey)
            || !contains(LoginTokenDao.expiresAtKey)
            || !contains(LoginTokenDao.idKey)
            || !contains((LoginTokenDao.emailKey))) {
            return Outcome.err(new NullPointerException());
        }
        return Outcome.ok(new LoginTokenDao(
            getString(LoginTokenDao.accessTokenKey),
            getString(LoginTokenDao.refreshTokenKey),
            getLong(LoginTokenDao.expiresAtKey),
            getString(LoginTokenDao.idKey),
            getString(LoginTokenDao.emailKey)
        ));
    }
}
