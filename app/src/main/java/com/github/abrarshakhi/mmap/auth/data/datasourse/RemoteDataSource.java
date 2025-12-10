package com.github.abrarshakhi.mmap.auth.data.datasourse;

import com.github.abrarshakhi.mmap.auth.data.model.UserDto;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RemoteDataSource {

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public RemoteDataSource() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public Task<AuthResult> signup(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password);
    }

    public Task<Void> saveUserProfile(String uid, UserDto userDto) {
        return db.collection("users")
            .document(uid)
            .set(userDto);
    }

    public Task<UserDto> fetchUserProfile(String uid) {
        return db.collection("users")
            .document(uid)
            .get()
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    return null;
                }
                return task.getResult().toObject(UserDto.class);
            });
    }

    public Task<Void> sendEmailVerification(FirebaseUser user) {
        if (user != null) {
            return user.sendEmailVerification();
        } else {
            return Tasks.forException(new Exception("No current user"));
        }
    }

    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isEmailVerified(FirebaseUser user) {
        return (user != null) && user.isEmailVerified();
    }

    public void logout() {
        auth.signOut();
    }
}