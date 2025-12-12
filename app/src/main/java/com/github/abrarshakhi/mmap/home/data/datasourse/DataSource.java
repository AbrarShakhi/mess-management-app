package com.github.abrarshakhi.mmap.home.data.datasourse;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.auth.data.datasourse.AuthDataSource;
import com.github.abrarshakhi.mmap.home.data.dto.MessDto;
import com.github.abrarshakhi.mmap.home.data.dto.MessMemberDto;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DataSource extends AuthDataSource {

    public DataSource(Context context) {
        super(context);
        sp = getSharedPreferences(context);
    }

    public void  saveCurrentMessId(String messId) {
        sp.edit().putString("CURR_MESS", messId).apply();
    }

    public String getCurrentMessId() {
        return sp.getString("CURR_MESS", "");
    }

    public void cleanPrefs() {
        sp.edit().clear().apply();
    }

    public MessDto createMess(@NonNull MessDto dto) throws ExecutionException, InterruptedException {
        String id = db.collection("mess").document().getId();
        dto.messId = id;

        Tasks.await(db.collection("mess").document(id).set(dto));
        return dto;
    }

    public MessDto getMess(String messId) throws ExecutionException, InterruptedException {
        var task = db.collection("mess").document(messId)
                .get()
                .continueWith(t -> t.getResult().toObject(MessDto.class));

        Tasks.await(task);
        return task.getResult();
    }

    public List<MessDto> getMessesForUser(String userId) throws ExecutionException, InterruptedException {
        var task = db.collectionGroup("mess_members")
                .whereEqualTo("userId", userId)
                .get();

        Tasks.await(task);

        List<MessDto> list = new ArrayList<>();

        for (QueryDocumentSnapshot doc : task.getResult()) {
            String messId = doc.getReference().getParent().getParent().getId();
            MessDto mess = getMess(messId);
            if (mess != null) list.add(mess);
        }

        return list;
    }

    public void addMember(String messId, @NonNull MessMemberDto dto) throws ExecutionException, InterruptedException {
        Tasks.await(
                db.collection("mess")
                        .document(messId)
                        .collection("mess_members")
                        .document(dto.userId)
                        .set(dto)
        );
    }

    public List<MessMemberDto> getMembers(String messId) throws ExecutionException, InterruptedException {
        var task = db.collection("mess")
                .document(messId)
                .collection("mess_members")
                .get();

        Tasks.await(task);

        List<MessMemberDto> list = new ArrayList<>();
        task.getResult().forEach(doc -> list.add(doc.toObject(MessMemberDto.class)));
        return list;
    }
}
