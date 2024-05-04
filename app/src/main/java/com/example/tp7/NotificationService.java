package com.example.tp7;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationService {
    private static final String CHANNEL_ID = "FlashlightChannel";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Flashlight Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setLightColor(Color.RED);
            serviceChannel.enableVibration(false);
            serviceChannel.setShowBadge(false);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public static Notification getNotification(Context context) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent, 0);

        Intent stopIntent = new Intent(context, FlashlightService.class);
        stopIntent.setAction(Constants.ACTION_STOP_FOREGROUND_SERVICE);
        PendingIntent pendingStopIntent = PendingIntent.getService(context,
                0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setContentTitle("Flashlight Running")
                .setContentText("Click to open the app.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, "Stop", pendingStopIntent);

        return builder.build();
    }
}
