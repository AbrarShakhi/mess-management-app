package com.github.abrarshakhi.mmap.core.storage;

import android.content.SharedPreferences;

public abstract class LocalPreferences {
    protected static LocalPreferences instance;
    private final SharedPreferences sp;

    protected LocalPreferences(SharedPreferences sharedPreferences) {
        this.sp = sharedPreferences;
    }

    protected void saveString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    protected void saveLong(String key, Long value) {
        sp.edit().putLong(key, value).apply();
    }

    protected String getString(String key) {
        return sp.getString(key, null);
    }

    protected boolean contains(String key) {
        return sp.contains(key);
    }

    protected Long getLong(String key) {
        return sp.getLong(key, 0);
    }

    protected void remove(String key) {
        sp.edit().remove(key).apply();
    }

    public void clearAll() {
        sp.edit().clear().apply();
    }
}
