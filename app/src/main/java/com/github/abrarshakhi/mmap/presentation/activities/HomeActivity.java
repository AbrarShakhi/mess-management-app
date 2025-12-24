package com.github.abrarshakhi.mmap.presentation.activities;

import android.Manifest;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.MMAp;
import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.core.constants.MessMemberRole;
import com.github.abrarshakhi.mmap.data.datasourse.HomeDataSource;
import com.github.abrarshakhi.mmap.data.dto.PaymentDto;
import com.github.abrarshakhi.mmap.databinding.ActivityHomeBinding;
import com.github.abrarshakhi.mmap.presentation.navigations.NavDestination;
import com.github.abrarshakhi.mmap.presentation.navigations.NavigationManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeActivity extends AppCompatActivity {

    private static final int REQ_POST_NOTIFICATION = 1001;
    ListenerRegistration paymentNotifyListener;
    private ActivityHomeBinding binding;
    private NavigationManager navigation;
    private HomeDataSource homeDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        requestNotificationPermissionIfNeeded();
        homeDataSource = new HomeDataSource(getApplicationContext());

        // Apply system window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.topAppBar);

        navigation = new NavigationManager(
                getSupportFragmentManager(),
                R.id.flMainFrameLayout,
                getSupportActionBar()
        );

        checkUserMess();

        binding.bnvMainNavBar.setOnItemSelectedListener(item -> {
            navigation.navigate(NavDestination.fromMenuId(item.getItemId()));
            return true;
        });

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
            Toast.makeText(this, "Enable notifications to stay updated", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Check if the current user has a mess, and navigate accordingly.
     */
    private void checkUserMess() {
        String currentMessId = homeDataSource.getCurrentMessId();

        if (currentMessId != null && !currentMessId.isBlank()) {
            homeDataSource.getMess(currentMessId, messDto -> {
                if (messDto != null) {
                    FirebaseMessaging.getInstance()
                            .subscribeToTopic("mess_" + homeDataSource.getCurrentMessId());
                    navigation.navigate();
                } else {
                    navigateToAddMess();
                }
            }, e -> {
                Toast.makeText(HomeActivity.this,
                        "Error fetching mess: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                navigateToAddMess();
            });
        } else {
            setAnyJoinedMess();
        }
    }

    private void setAnyJoinedMess() {
        var loggedInUserId = homeDataSource.getLoggedInUser().getUid();
        homeDataSource.getMessesForUser(loggedInUserId, result -> {
            for (var messDto : result) {
                for (var member : messDto.members) {
                    if (member.userId.equals(loggedInUserId) && !member.role.equals(MessMemberRole.LEFT)) {
                        homeDataSource.saveCurrentMessId(messDto.messId);
                        listenForNewPayments();
                        return;
                    }
                }
            }
            navigateToAddMess();
        }, e -> navigateToAddMess());
    }

    private void navigateToAddMess() {
        startActivity(new Intent(HomeActivity.this, AddMessActivity.class));
        finish();
    }

    private void listenForNewPayments() {
        paymentNotifyListener = FirebaseFirestore.getInstance()
                .collection("mess")
                .document(homeDataSource.getCurrentMessId())
                .collection("payments")
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) return;

                    for (var change : snap.getDocumentChanges()) {
                        if (change.getType() == DocumentChange.Type.ADDED) {
                            PaymentDto p = change.getDocument().toObject(PaymentDto.class);
                            if (!p.userId.equals(homeDataSource.getLoggedInUser().getUid())) {
                                showNotification(p);
                                navigation.navigate();
                            }
                        }
                    }
                });
    }

    private void showNotification(PaymentDto p) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Notification notification =
                new NotificationCompat.Builder(this, MMAp.PAYMENT_CHANNEL)
                        .setSmallIcon(R.drawable.ic_money)
                        .setContentTitle("New Payment")
                        .setContentText("BDT " + p.amount + " • " + p.type)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .build();

        NotificationManagerCompat.from(this)
                .notify((int) System.currentTimeMillis(), notification);
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_POST_NOTIFICATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications disabled", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQ_POST_NOTIFICATION
                );
            }
        } else {
            // add else block here maybe
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (paymentNotifyListener != null) paymentNotifyListener.remove();
    }

}
