package com.github.abrarshakhi.mmap.auth.data.utils;

public class AuthUtils {
    public static boolean isTokenExpired(long expiresAtInSec) {
        return System.currentTimeMillis() > (expiresAtInSec * 1_000) - 30_000;
    }

    public static boolean isTokenValid(long expiresAtInSec) {
        return !isTokenExpired(expiresAtInSec);
    }
}
