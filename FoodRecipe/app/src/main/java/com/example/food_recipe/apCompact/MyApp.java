package com.example.pj_off.apCompact;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pj_off.manager.LanguageManager;

public class MyApp extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageManager languageManager=new LanguageManager(MyApp.this);
        languageManager.updateResources(languageManager.getLanguage());
    }
}
