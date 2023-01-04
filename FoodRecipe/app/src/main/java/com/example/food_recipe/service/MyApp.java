package com.example.pj_off.service;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyApp extends Application {

    public static final String RECIPE_CHANNEL = "RECIPE_CHANNEL";
    public static NotificationManager manager;
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel=new NotificationChannel
                    (RECIPE_CHANNEL,"Recipe Service", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("This is Recipe Service");
            manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
