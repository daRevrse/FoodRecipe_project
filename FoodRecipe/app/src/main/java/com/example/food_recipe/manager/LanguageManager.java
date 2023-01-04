package com.example.pj_off.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LanguageManager {

    private Context context;
    SharedPreferences sharedPreferences;

    public LanguageManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);

    }

    public void updateResources(String code) {
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        setLanguage(code);

    }


    private void setLanguage(String code) {
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("app_lang", code);
        myEdit.apply();
    }

    public String getLanguage() {
        String lan = sharedPreferences.getString("app_lang", "en");
        return lan;
    }
}
