package com.github.abrarshakhi.mmap.auth.data.local.storage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.github.abrarshakhi.mmap.auth.data.local.dto.LoginTokenDto;
import com.github.abrarshakhi.mmap.core.storage.LocalPreferences;
import com.github.abrarshakhi.mmap.core.utils.Outcome;

import org.jetbrains.annotations.NotNull;

public class AuthStorage extends LocalPreferences {
    private static final String FILE_NAME = "login_credentials";
    private static AuthStorage instance;

    private AuthStorage(SharedPreferences sharedPreferences) {
        super(sharedPreferences);
    }

    public static synchronized Outcome<AuthStorage, Throwable> getInstance(@NotNull Context context) {
        if (instance == null) {
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
            instance = new AuthStorage(outcome.unwrap());
        }
        return Outcome.ok(instance);
    }

    public void saveToken(LoginTokenDto token) {
        saveString(LoginTokenDto.accessTokenKey, token.getAccessToken());
        saveString(LoginTokenDto.refreshTokenKey, token.getRefreshToken());
        saveLong(LoginTokenDto.expiresAtKey, token.getExpiresAt());
    }

    public Outcome<LoginTokenDto, NullPointerException> loadToken() {
        if (!contains(LoginTokenDto.accessTokenKey)
                || !contains(LoginTokenDto.refreshTokenKey)
                || !contains(LoginTokenDto.expiresAtKey)) {
            return Outcome.err(new NullPointerException());
        }
        return Outcome.ok(new LoginTokenDto(
                getString(LoginTokenDto.accessTokenKey),
                getString(LoginTokenDto.refreshTokenKey),
                getLong(LoginTokenDto.expiresAtKey)
        ));
    }
}
