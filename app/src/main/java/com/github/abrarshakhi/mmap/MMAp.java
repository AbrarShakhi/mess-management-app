package com.github.abrarshakhi.mmap;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;


public class MMAp extends Application {

    public static final String PAYMENT_CHANNEL = "payment_channel";

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();
        db.setFirestoreSettings(settings);

        NotificationChannel channel = new NotificationChannel(
                PAYMENT_CHANNEL,
                "Payments",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Payment notifications");

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}
