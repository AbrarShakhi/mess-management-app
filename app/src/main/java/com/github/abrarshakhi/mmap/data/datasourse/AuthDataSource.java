package com.github.abrarshakhi.mmap.data.datasourse;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.abrarshakhi.mmap.data.dto.UserDto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthDataSource {

    protected final FirebaseAuth auth;
    protected final FirebaseFirestore db;
    protected final SharedPreferences sp;

    public AuthDataSource(Context context) {
        FirebaseApp.initializeApp(context);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sp = context.getSharedPreferences("APP_STATE", MODE_PRIVATE);
    }

    /* ---------- AUTH ---------- */

    public void signup(
            String email,
            String password,
            OnSuccessListener<AuthResult> success,
            OnFailureListener failure
    ) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(success)
                .addOnFailureListener(failure);
    }

    public void login(
            String email,
            String password,
            OnSuccessListener<AuthResult> success,
            OnFailureListener failure
    ) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(success)
                .addOnFailureListener(failure);
    }

    public void saveUserProfile(
            String uid,
            UserDto dto,
            OnSuccessListener<Void> success,
            OnFailureListener failure
    ) {
        db.collection("users")
                .document(uid)
                .set(dto)
                .addOnSuccessListener(success)
                .addOnFailureListener(failure);
    }

    public void fetchUserProfile(
            String uid,
            OnSuccessListener<UserDto> success,
            OnFailureListener failure
    ) {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(snap ->
                        success.onSuccess(snap.toObject(UserDto.class))
                )
                .addOnFailureListener(failure);
    }

    public void sendEmailVerification(
            FirebaseUser user,
            OnSuccessListener<Void> success,
            OnFailureListener failure
    ) {
        if (user == null) {
            failure.onFailure(new Exception("User null"));
            return;
        }
        user.sendEmailVerification()
                .addOnSuccessListener(success)
                .addOnFailureListener(failure);
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public FirebaseUser getLoggedInUser() {
        return auth.getCurrentUser();
    }

    public void logout() {
        auth.signOut();
    }
}
