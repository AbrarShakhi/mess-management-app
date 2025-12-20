package com.github.abrarshakhi.mmap.data.datasourse;

import android.content.Context;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.data.dto.GroceryBatchDto;
import com.github.abrarshakhi.mmap.data.dto.MessDto;
import com.github.abrarshakhi.mmap.domain.model.MonthYear;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

public class HomeDataSource extends AuthDataSource {

    public HomeDataSource(Context context) {
        super(context);
    }

    /* ---------- PREFS ---------- */

    public void saveCurrentMessId(String messId) {
        sp.edit().putString("CURR_MESS", messId).apply();
    }

    public void clear() {
        sp.edit().clear().apply();
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
        @NonNull GroceryBatchDto dto,
        OnSuccessListener<GroceryBatchDto> success,
        OnFailureListener failure
    ) {
        String messId = dto.messId;

        String batchId = db.collection("mess")
            .document(messId)
            .collection("grocery_batches")
            .document()
            .getId();

        dto.batchId = batchId;

        db.collection("mess")
            .document(messId)
            .collection("grocery_batches")
            .document(batchId)
            .set(dto)
            .addOnSuccessListener(v -> success.onSuccess(dto))
            .addOnFailureListener(failure);
    }

    public ListenerRegistration addGroceryOfflineFirst(
        @NonNull GroceryBatchDto dto,
        OnSuccessListener<GroceryBatchDto> localSuccess,
        OnSuccessListener<GroceryBatchDto> serverSuccess,
        OnFailureListener failure
    ) {
        DocumentReference ref = db.collection("mess")
            .document(dto.messId)
            .collection("grocery_batches")
            .document();

        dto.batchId = ref.getId();

        // Write (works offline)
        ref.set(dto).addOnFailureListener(failure);

        // Listen to local + server state
        return ref.addSnapshotListener((snap, e) -> {
            if (e != null || snap == null) return;

            if (snap.getMetadata().hasPendingWrites()) {
                // ✅ Offline / local success
                localSuccess.onSuccess(dto);
            } else {
                // ✅ Synced with server
                serverSuccess.onSuccess(dto);
            }
        });
    }



    public void getGroceries(
        String messId,
        int month,
        int year,
        OnSuccessListener<List<GroceryBatchDto>> success,
        OnFailureListener failure
    ) {
        Query query = db.collection("mess")
            .document(messId)
            .collection("grocery_batches")
            .whereEqualTo("month", month)
            .whereEqualTo("year", year);

        query.get(Source.CACHE)
            .addOnSuccessListener(snap -> {
                List<GroceryBatchDto> list = new ArrayList<>();
                snap.forEach(d -> list.add(d.toObject(GroceryBatchDto.class)));
                success.onSuccess(list);
            })
            .addOnFailureListener(e ->
                query.get(Source.SERVER)
                    .addOnSuccessListener(snap -> {
                        List<GroceryBatchDto> list = new ArrayList<>();
                        snap.forEach(d -> list.add(d.toObject(GroceryBatchDto.class)));
                        success.onSuccess(list);
                    })
                    .addOnFailureListener(failure)
            );
    }

    public ListenerRegistration listenGroceriesRealtime(
            String messId,
            int month,
            int year,
            OnSuccessListener<List<GroceryBatchDto>> success,
            OnFailureListener failure
    ) {
        Query query = db.collection("mess")
                .document(messId)
                .collection("grocery_batches")
                .whereEqualTo("month", month)
                .whereEqualTo("year", year);

        return query.addSnapshotListener((snap, e) -> {
            if (e != null) {
                failure.onFailure(e);
                return;
            }

            if (snap != null) {
                List<GroceryBatchDto> list = new ArrayList<>();
                snap.forEach(d -> list.add(d.toObject(GroceryBatchDto.class)));
                success.onSuccess(list);
            }
        });
    }


    public void getMessesForUser(
        @NonNull String userId,
        @NonNull OnSuccessListener<List<MessDto>> success,
        @NonNull OnFailureListener failure
    ) {
        db.collection("mess")
            .whereArrayContains("memberIds", userId)
            .get(Source.CACHE)
            .addOnSuccessListener(snap -> {
                List<MessDto> messList = new ArrayList<>();
                snap.forEach(doc -> messList.add(doc.toObject(MessDto.class)));
                success.onSuccess(messList);
            })
            .addOnFailureListener(e ->
                db.collection("mess")
                    .whereArrayContains("memberIds", userId)
                    .get(Source.SERVER)
                    .addOnSuccessListener(snap -> {
                        List<MessDto> messList = new ArrayList<>();
                        snap.forEach(doc -> messList.add(doc.toObject(MessDto.class)));
                        success.onSuccess(messList);
                    })
                    .addOnFailureListener(failure)
            );
    }

    /* ---------- MONTH YEAR ---------- */

    public void saveCurrentMonthYear(int month, int year) {
        sp.edit()
            .putInt("CURR_MONTH", month)
            .putInt("CURR_YEAR", year)
            .apply();
    }

    public Outcome<MonthYear, String> getCurrentMonthYear() {
        int month = sp.getInt("CURR_MONTH", -1);
        int year = sp.getInt("CURR_YEAR", -1);

        if (month == -1 || year == -1) {
            return Outcome.err("MonthYear not found");
        }
        return MonthYear.newValidInstance(month, year);
    }
    /* ---------- MONTH YEAR FROM MESS ---------- */

    public void getCurrentMonthYearFromMess(
        @NonNull String messId,
        @NonNull OnSuccessListener<MonthYear> success,
        @NonNull OnFailureListener failure
    ) {
        getMess(
            messId,
            mess -> {
                Outcome<MonthYear, String> outcome = MonthYear.newValidInstance(mess.month, mess.year);

                if (outcome.isOK()) {
                    success.onSuccess(outcome.unwrap());
                } else {
                    failure.onFailure(
                        new IllegalArgumentException(outcome.unwrapErr())
                    );
                }
            },
            failure
        );
    }

    public void deleteMess(String messId, OnSuccessListener<Void> success, OnFailureListener failure) {
        db.collection("mess")
            .document(messId)
            .delete()
            .addOnSuccessListener(success)
            .addOnFailureListener(failure);
    }

}
