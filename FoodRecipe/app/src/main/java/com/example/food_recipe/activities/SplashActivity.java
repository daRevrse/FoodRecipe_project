package com.example.pj_off.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.databinding.DataBindingUtil;

import com.example.pj_off.R;
import com.example.pj_off.apCompact.MyApp;
import com.example.pj_off.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends MyApp {

    ActivitySplashBinding splashBinding;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashBinding= DataBindingUtil.setContentView(this,R.layout.activity_splash);

        fAuth = FirebaseAuth.getInstance();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (fAuth.getCurrentUser() !=null) {
                    startActivity(new Intent(getApplicationContext(), ShowResultActivity.class));
                    finish();

                }
                else {
                    startActivity(new Intent(SplashActivity.this, Login.class));
                }
            }
        },2000);
    }
}