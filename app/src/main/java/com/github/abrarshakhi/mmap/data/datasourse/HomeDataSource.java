package com.github.abrarshakhi.mmap.data.datasourse;

import android.content.Context;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.core.constants.MessMemberRole;
import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.data.dto.GroceryBatchDto;
import com.github.abrarshakhi.mmap.data.dto.MealTrackingDto;
import com.github.abrarshakhi.mmap.data.dto.MessDto;
import com.github.abrarshakhi.mmap.data.dto.MessMemberDto;
import com.github.abrarshakhi.mmap.data.dto.PaymentDto;
import com.github.abrarshakhi.mmap.domain.model.MessMember;
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

    public void findUserByEmail(
        String email,
        OnSuccessListener<String> success, // returns userId
        OnFailureListener failure
    ) {
        db.collection("users")
            .whereEqualTo("email", email)
            .limit(1)
            .get(com.google.firebase.firestore.Source.CACHE)
            .addOnSuccessListener(snap -> {
                if (!snap.isEmpty()) {
                    success.onSuccess(snap.getDocuments().get(0).getId());
                } else {
                    db.collection("users")
                        .whereEqualTo("email", email)
                        .limit(1)
                        .get(com.google.firebase.firestore.Source.SERVER)
                        .addOnSuccessListener(serverSnap -> {
                            if (!serverSnap.isEmpty()) {
                                success.onSuccess(
                                    serverSnap.getDocuments().get(0).getId()
                                );
                            } else {
                                failure.onFailure(
                                    new Exception("User does not exist")
                                );
                            }
                        })
                        .addOnFailureListener(failure);
                }
            })
            .addOnFailureListener(failure);
    }


    public ListenerRegistration listenMessMembersRealtime(
        @NonNull String messId,
        @NonNull OnSuccessListener<List<MessMember>> success,
        @NonNull OnFailureListener failure) {
        DocumentReference messRef = db.collection("mess").document(messId);

        return messRef.addSnapshotListener((snap, e) -> {
            if (e != null || snap == null || !snap.exists()) {
                failure.onFailure(e != null ? e : new Exception("Mess not found"));
                return;
            }

            MessDto mess = snap.toObject(MessDto.class);
            if (mess == null || mess.members == null) {
                success.onSuccess(new ArrayList<>());
                return;
            }

            List<MessMember> members = new ArrayList<>();
            int total = mess.members.size();

            if (total == 0) {
                success.onSuccess(members);
                return;
            }

            for (MessMemberDto memberDto : mess.members) {
                fetchUserProfile(memberDto.userId, userDto -> {
                    String fullName = userDto != null ? userDto.fullName : "Unknown";

                    MessMember member = new MessMember(
                        memberDto.userId,
                        memberDto.messId,
                        memberDto.role,
                        memberDto.joinedAt,
                        memberDto.houseRent,
                        memberDto.utility,
                        fullName
                    );

                    members.add(member);

                    if (members.size() == total) {
                        success.onSuccess(members);
                    }
                }, failure);
            }
        });
    }

    public void editMemberOfflineCapable(
        String messId,
        MessMemberDto updatedMember,
        OnSuccessListener<String> success,
        OnFailureListener failure
    ) {
        DocumentReference messRef = db.collection("mess").document(messId);

        // Use default fetch (offline first)
        messRef.get().addOnSuccessListener(snap -> {
            if (!snap.exists()) {
                failure.onFailure(new IllegalStateException("Mess does not exist"));
                return;
            }

            MessDto mess = snap.toObject(MessDto.class);
            if (mess == null || mess.members == null) {
                failure.onFailure(new IllegalStateException("Mess data corrupted"));
                return;
            }

            boolean found = false;
            for (int i = 0; i < mess.members.size(); i++) {
                MessMemberDto m = mess.members.get(i);
                if (m.userId.equals(updatedMember.userId)) {
                    mess.members.set(i, updatedMember); // Update member
                    found = true;
                    break;
                }
            }

            if (!found) {
                failure.onFailure(new IllegalStateException("Member not found in mess"));
                return;
            }

            // Set the updated mess (offline first, syncs automatically)
            messRef.set(mess)
                .addOnSuccessListener(v -> success.onSuccess("Member updated successfully"))
                .addOnFailureListener(failure);

        }).addOnFailureListener(failure);
    }

    public ListenerRegistration addOrUpdateMemberOffline(
        String messId,
        MessMemberDto newMember,
        OnSuccessListener<String> localSuccess,
        OnSuccessListener<String> serverSuccess,
        OnFailureListener failure
    ) {
        DocumentReference messRef = db.collection("mess").document(messId);

        return messRef.addSnapshotListener((snap, e) -> {
            if (e != null || snap == null || !snap.exists()) return;

            MessDto mess = snap.toObject(MessDto.class);
            if (mess == null) return;

            boolean found = false;

            for (MessMemberDto m : mess.members) {
                if (m.userId.equals(newMember.userId)) {
                    found = true;

                    if (!m.role.equals(MessMemberRole.LEFT)) {
                        failure.onFailure(
                            new IllegalStateException("User already active in mess")
                        );
                        return;
                    }
                    m.role = MessMemberRole.DEFAULT;
                }
            }

            if (!found) {
                mess.members.add(newMember);
                mess.memberIds.add(newMember.userId);
            }

            messRef.set(mess)
                .addOnFailureListener(failure);

            if (snap.getMetadata().hasPendingWrites()) {
                localSuccess.onSuccess("Saved locally (offline)");
            } else {
                serverSuccess.onSuccess("Synced with server");
            }
        });
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

    public ListenerRegistration addPaymentOfflineFirst(
        @NonNull PaymentDto dto,
        @NonNull OnSuccessListener<PaymentDto> localSuccess,
        @NonNull OnSuccessListener<PaymentDto> serverSuccess,
        @NonNull OnFailureListener failure
    ) {
        DocumentReference ref = db.collection("mess")
            .document(dto.messId)
            .collection("payments")
            .document();

        dto.paymentId = ref.getId();
        dto.timestamp = System.currentTimeMillis();

        ref.set(dto).addOnFailureListener(failure);

        return ref.addSnapshotListener((snap, e) -> {
            if (e != null || snap == null) return;

            if (snap.getMetadata().hasPendingWrites()) {
                localSuccess.onSuccess(dto);
            } else {
                serverSuccess.onSuccess(dto);
            }
        });
    }

    public void getPayments(
        @NonNull String messId,
        int month,
        int year,
        @NonNull OnSuccessListener<List<PaymentDto>> success,
        @NonNull OnFailureListener failure
    ) {
        Query query = db.collection("mess")
            .document(messId)
            .collection("payments")
            .whereEqualTo("month", month)
            .whereEqualTo("year", year);

        query.get(Source.CACHE)
            .addOnSuccessListener(snap -> {
                List<PaymentDto> list = new ArrayList<>();
                snap.forEach(d -> list.add(d.toObject(PaymentDto.class)));
                success.onSuccess(list);
            })
            .addOnFailureListener(e ->
                query.get(Source.SERVER)
                    .addOnSuccessListener(snap -> {
                        List<PaymentDto> list = new ArrayList<>();
                        snap.forEach(d -> list.add(d.toObject(PaymentDto.class)));
                        success.onSuccess(list);
                    })
                    .addOnFailureListener(failure)
            );
    }

    public ListenerRegistration listenMealsRealtime(
        @NonNull String messId,
        int month,
        int year,
        @NonNull OnSuccessListener<List<MealTrackingDto>> success,
        @NonNull OnFailureListener failure
    ) {
        Query query = db.collection("mess")
            .document(messId)
            .collection("meal_tracking")
            .whereEqualTo("month", month)
            .whereEqualTo("year", year);

        return query.addSnapshotListener((snap, e) -> {
            if (e != null) {
                failure.onFailure(e);
                return;
            }

            if (snap != null) {
                List<MealTrackingDto> list = new ArrayList<>();
                snap.forEach(d -> list.add(d.toObject(MealTrackingDto.class)));
                success.onSuccess(list);
            }
        });
    }

    public void getMeals(
        @NonNull String messId,
        int month,
        int year,
        @NonNull OnSuccessListener<List<MealTrackingDto>> success,
        @NonNull OnFailureListener failure
    ) {
        Query query = db.collection("mess")
            .document(messId)
            .collection("meal_tracking")
            .whereEqualTo("month", month)
            .whereEqualTo("year", year);

        query.get(Source.CACHE)
            .addOnSuccessListener(snap -> {
                List<MealTrackingDto> list = new ArrayList<>();
                snap.forEach(d -> list.add(d.toObject(MealTrackingDto.class)));
                success.onSuccess(list);
            })
            .addOnFailureListener(e ->
                query.get(Source.SERVER)
                    .addOnSuccessListener(snap -> {
                        List<MealTrackingDto> list = new ArrayList<>();
                        snap.forEach(d -> list.add(d.toObject(MealTrackingDto.class)));
                        success.onSuccess(list);
                    })
                    .addOnFailureListener(failure)
            );
    }


    public ListenerRegistration addMealOfflineFirst(
        @NonNull MealTrackingDto dto,
        @NonNull OnSuccessListener<MealTrackingDto> localSuccess,
        @NonNull OnSuccessListener<MealTrackingDto> serverSuccess,
        @NonNull OnFailureListener failure
    ) {
        DocumentReference ref = db.collection("mess")
            .document(dto.messId)
            .collection("meal_tracking")
            .document();

        dto.mealId = ref.getId();
        dto.timestamp = System.currentTimeMillis();

        // Offline-capable write
        ref.set(dto).addOnFailureListener(failure);

        // Listen for local + server state
        return ref.addSnapshotListener((snap, e) -> {
            if (e != null || snap == null) return;

            if (snap.getMetadata().hasPendingWrites()) {
                // ✅ local / offline
                localSuccess.onSuccess(dto);
            } else {
                // ✅ synced with server
                serverSuccess.onSuccess(dto);
            }
        });
    }


    public ListenerRegistration listenPaymentsRealtime(
        @NonNull String messId,
        int month,
        int year,
        @NonNull OnSuccessListener<List<PaymentDto>> success,
        @NonNull OnFailureListener failure
    ) {
        Query query = db.collection("mess")
            .document(messId)
            .collection("payments")
            .whereEqualTo("month", month)
            .whereEqualTo("year", year);

        return query.addSnapshotListener((snap, e) -> {
            if (e != null) {
                failure.onFailure(e);
                return;
            }

            if (snap != null) {
                List<PaymentDto> list = new ArrayList<>();
                snap.forEach(d -> list.add(d.toObject(PaymentDto.class)));
                success.onSuccess(list);
            }
        });
    }

}
