package com.github.abrarshakhi.mmap.auth.data.datasourse;

import android.content.Context;

import com.github.abrarshakhi.mmap.auth.data.model.UserDto;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutionException;

public class RemoteDataSource {

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public RemoteDataSource(Context context) {
        FirebaseApp.initializeApp(context);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public Task<AuthResult> signup(String email, String password) throws ExecutionException, InterruptedException {
        var asyncTask = auth.createUserWithEmailAndPassword(email, password);
        Tasks.await(asyncTask);
        return asyncTask;
    }

    public Task<Void> saveUserProfile(String uid, UserDto userDto) throws ExecutionException, InterruptedException {
        var asyncTask = db.collection("users")
            .document(uid)
            .set(userDto);
        Tasks.await(asyncTask);
        return asyncTask;
    }

    public Task<UserDto> fetchUserProfile(String uid) throws ExecutionException, InterruptedException {
        var asyncTask = db.collection("users")
            .document(uid)
            .get()
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    return null;
                }
                return task.getResult().toObject(UserDto.class);
            });
        Tasks.await(asyncTask);
        return asyncTask;
    }

    public Task<Void> sendEmailVerification(FirebaseUser user) throws ExecutionException, InterruptedException {
        if (user != null) {
            var asyncTask = user.sendEmailVerification();
            Tasks.await(asyncTask);
            return asyncTask;
        } else {
            return Tasks.forException(new Exception("No current user"));
        }
    }

    public Task<AuthResult> login(String email, String password) throws ExecutionException, InterruptedException {
        var asyncTask = auth.signInWithEmailAndPassword(email, password);
        Tasks.await(asyncTask);
        return asyncTask;
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