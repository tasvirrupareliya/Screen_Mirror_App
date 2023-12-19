package com.example.screenmirror;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class BackgroundService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Intent intent1 = new Intent(String.valueOf(this));
        Intent intent2 = new Intent(String.valueOf(this));


        //this code is for running app in background

        PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0, intent2, PendingIntent.FLAG_IMMUTABLE);


        Notification notification1 = new NotificationCompat.Builder(this, "ScreenRecorder")
                .setContentTitle("yNote studios")
                .setContentText("Filming...")
                .setContentIntent(pendingIntent1).build();

        Notification notification2 = new NotificationCompat.Builder(this, "ScreenRecorder")
                .setContentTitle("yNote studios")
                .setContentText("Filming...")
                .setContentIntent(pendingIntent1).build();

        startForeground(1, notification1);
        startForeground(2, notification2);
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("ScreenRecorder", "Foreground notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();

        super.onDestroy();
    }
}