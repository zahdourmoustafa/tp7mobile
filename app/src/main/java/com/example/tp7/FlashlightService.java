// FlashlightService.java
package com.example.tp7;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class FlashlightService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "Flashlight_Channel";
    private static boolean isRunning = false;

    private CameraManager cameraManager;
    private String cameraId;

    public static void start(Context context) {
        if (!isRunning) {
            Intent serviceIntent = new Intent(context, FlashlightService.class);
            serviceIntent.setAction(Constants.ACTION_START_FOREGROUND_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }

    public static void stop(Context context) {
        if (isRunning) {
            Intent serviceIntent = new Intent(context, FlashlightService.class);
            serviceIntent.setAction(Constants.ACTION_STOP_FOREGROUND_SERVICE);
            context.startService(serviceIntent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(Constants.ACTION_START_FOREGROUND_SERVICE)) {
                startForegroundService();
                startFlashlight();
            } else if (intent.getAction().equals(Constants.ACTION_STOP_FOREGROUND_SERVICE)) {
                stopForeground(true);
                stopSelf();
                stopFlashlight();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("ForegroundServiceType")
    private void startForegroundService() {
        Notification notification = buildNotification();
        startForeground(NOTIFICATION_ID, notification);
        isRunning = true;
    }

    private Notification buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Flashlight Service")
                .setContentText("Flashlight is ON")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent stopIntent = new Intent(this, FlashlightService.class);
        stopIntent.setAction(Constants.ACTION_STOP_FOREGROUND_SERVICE);
        PendingIntent pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        builder.addAction(R.drawable.ic_launcher_foreground, "Stop", pendingStopIntent);

        return builder.build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Flashlight Channel";
            String description = "Channel for controlling flashlight";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void startFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void stopFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        stopForeground(true);
    }
}
