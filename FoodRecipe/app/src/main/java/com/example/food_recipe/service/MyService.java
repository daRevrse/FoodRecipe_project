package com.example.pj_off.service;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.pj_off.R;
import com.example.pj_off.activities.ShowResultActivity;

public class MyService extends Service  {
    Notification notification;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServiceForeground(intent);


        return START_STICKY;
    }
    private void startServiceForeground(Intent intent)
    {
        //Create Intent fow which class we are open
        intent=new Intent(this, ShowResultActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 123, intent, PendingIntent.FLAG_IMMUTABLE);

        //Create Notification
        notification = new NotificationCompat.Builder(this, MyApp.RECIPE_CHANNEL)
                .setContentTitle("Recipe Service")
                .setSubText("")
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOnlyAlertOnce(true)
                .build();
        startForeground(1,notification);
        Toast.makeText(getApplicationContext(), "Start Service ", Toast.LENGTH_SHORT).show();
    }


}
