package com.github.abrarshakhi.mmap.home.data.datasourse;

import android.content.Context;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.auth.data.datasourse.AuthDataSource;
import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.home.data.dto.GroceryDto;
import com.github.abrarshakhi.mmap.home.data.dto.MessDto;
import com.github.abrarshakhi.mmap.home.data.dto.MessMemberDto;
import com.github.abrarshakhi.mmap.home.domain.model.GroceryBatch;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DataSource extends AuthDataSource {

    public DataSource(Context context) {
        super(context);
        sp = getSharedPreferences(context);
    }

    public void saveCurrentMessId(String messId) {
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
            MessMemberDto member = doc.toObject(MessMemberDto.class);
            MessDto mess = getMess(member.messId);
            if (mess != null) list.add(mess);
        }

        return list;
    }

    public void addMember(String messId, @NonNull MessMemberDto dto)
        throws ExecutionException, InterruptedException {

        dto.messId = messId;

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

    public void deleteMess(String messId)
        throws ExecutionException, InterruptedException {

        // 1. Get all members
        var membersTask = db.collection("mess")
            .document(messId)
            .collection("mess_members")
            .get();

        Tasks.await(membersTask);

        // 2. Delete members safely (chunked)
        deleteCollection(membersTask.getResult());

        // 3. Get all groceries
        var groceriesTask = db.collection("mess")
            .document(messId)
            .collection("groceries")
            .get();

        Tasks.await(groceriesTask);

        // 4. Delete groceries safely (chunked)
        deleteCollection(groceriesTask.getResult());

        // 5. Delete mess document
        Tasks.await(
            db.collection("mess")
                .document(messId)
                .delete()
        );

        // 6. Clear current mess if needed
        if (messId.equals(getCurrentMessId())) {
            saveCurrentMessId("");
        }
    }


    private void deleteCollection(
        QuerySnapshot snapshot
    ) throws ExecutionException, InterruptedException {

        WriteBatch batch = db.batch();
        int count = 0;

        for (QueryDocumentSnapshot doc : snapshot) {
            batch.delete(doc.getReference());
            count++;

            if (count == 500) {
                Tasks.await(batch.commit());
                batch = db.batch();
                count = 0;
            }
        }

        if (count > 0) {
            Tasks.await(batch.commit());
        }
    }

    public GroceryDto addGrocery(
        @NonNull String messId,
        @NonNull GroceryDto dto
    ) throws ExecutionException, InterruptedException {

        String id = db.collection("mess")
            .document(messId)
            .collection("groceries")
            .document()
            .getId();

        dto.groceryId = id;
        dto.messId = messId;

        Tasks.await(
            db.collection("mess")
                .document(messId)
                .collection("groceries")
                .document(id)
                .set(dto)
        );

        return dto;
    }

    public List<GroceryDto> getGroceries(
        String messId,
        int month,
        int year
    ) throws ExecutionException, InterruptedException {

        var task = db.collection("mess")
            .document(messId)
            .collection("groceries")
            .whereEqualTo("month", month)
            .whereEqualTo("year", year)
            .get();

        Tasks.await(task);

        List<GroceryDto> list = new ArrayList<>();
        task.getResult().forEach(doc ->
            list.add(doc.toObject(GroceryDto.class))
        );

        return list;
    }

    public List<GroceryDto> getUserGroceries(
        String userId
    ) throws ExecutionException, InterruptedException {

        var task = db.collectionGroup("groceries")
            .whereEqualTo("userId", userId)
            .get();

        Tasks.await(task);

        List<GroceryDto> list = new ArrayList<>();
        task.getResult().forEach(doc ->
            list.add(doc.toObject(GroceryDto.class))
        );

        return list;
    }

    public void addGroceriesBatch(@NonNull String messId, @NonNull List<GroceryDto> groceries)
        throws ExecutionException, InterruptedException {

        WriteBatch batch = db.batch();
        int count = 0;

        for (GroceryDto dto : groceries) {
            String id = db.collection("mess")
                .document(messId)
                .collection("groceries")
                .document()
                .getId();
            dto.groceryId = id;
            dto.messId = messId;

            batch.set(
                db.collection("mess")
                    .document(messId)
                    .collection("groceries")
                    .document(id),
                dto
            );

            count++;
            // Commit every 500 documents (Firestore batch limit)
            if (count == 500) {
                Tasks.await(batch.commit());
                batch = db.batch();
                count = 0;
            }
        }

        if (count > 0) {
            Tasks.await(batch.commit());
        }
    }


}
