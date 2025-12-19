package com.github.abrarshakhi.mmap.home.data.datasourse;

import android.content.Context;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.auth.data.datasourse.AuthDataSource;
import com.github.abrarshakhi.mmap.home.data.dto.GroceryDto;
import com.github.abrarshakhi.mmap.home.data.dto.MessDto;
import com.github.abrarshakhi.mmap.home.data.dto.MessMemberDto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class DataSource extends AuthDataSource {

    public DataSource(Context context) {
        super(context);
    }

    /* ---------- PREFS ---------- */

    public void saveCurrentMessId(String messId) {
        sp.edit().putString("CURR_MESS", messId).apply();
    }

    public String getCurrentMessId() {
        return sp.getString("CURR_MESS", "");
    }

    /* ---------- MESS ---------- */

    public void createMess(
            @NonNull MessDto dto,
            OnSuccessListener<MessDto> success,
            OnFailureListener failure
    ) {
        String id = db.collection("mess").document().getId();
        dto.messId = id;

        db.collection("mess")
                .document(id)
                .set(dto)
                .addOnSuccessListener(v -> success.onSuccess(dto))
                .addOnFailureListener(failure);
    }

    public void getMess(
            String messId,
            OnSuccessListener<MessDto> success,
            OnFailureListener failure
    ) {
        db.collection("mess")
                .document(messId)
                .get(Source.CACHE)
                .addOnSuccessListener(snap -> {
                    if (snap.exists()) {
                        success.onSuccess(snap.toObject(MessDto.class));
                    } else {
                        // fallback server
                        db.collection("mess")
                                .document(messId)
                                .get(Source.SERVER)
                                .addOnSuccessListener(s ->
                                        success.onSuccess(s.toObject(MessDto.class))
                                )
                                .addOnFailureListener(failure);
                    }
                })
                .addOnFailureListener(failure);
    }

    /* ---------- GROCERIES ---------- */

    public void addGrocery(
            String messId,
            GroceryDto dto,
            OnSuccessListener<GroceryDto> success,
            OnFailureListener failure
    ) {
        String id = db.collection("mess")
                .document(messId)
                .collection("groceries")
                .document()
                .getId();

        dto.groceryId = id;
        dto.messId = messId;

        db.collection("mess")
                .document(messId)
                .collection("groceries")
                .document(id)
                .set(dto)
                .addOnSuccessListener(v -> success.onSuccess(dto))
                .addOnFailureListener(failure);
    }

    public void getGroceries(
            String messId,
            int month,
            int year,
            OnSuccessListener<List<GroceryDto>> success,
            OnFailureListener failure
    ) {
        Query query = db.collection("mess")
                .document(messId)
                .collection("groceries")
                .whereEqualTo("month", month)
                .whereEqualTo("year", year);

        query.get(Source.CACHE)
                .addOnSuccessListener(snap -> {
                    List<GroceryDto> list = new ArrayList<>();
                    snap.forEach(d -> list.add(d.toObject(GroceryDto.class)));
                    success.onSuccess(list);
                })
                .addOnFailureListener(e ->
                        query.get(Source.SERVER)
                                .addOnSuccessListener(snap -> {
                                    List<GroceryDto> list = new ArrayList<>();
                                    snap.forEach(d -> list.add(d.toObject(GroceryDto.class)));
                                    success.onSuccess(list);
                                })
                                .addOnFailureListener(failure)
                );
    }
}
