package com.appman.intern;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.appman.intern.enums.Language;

public class AppManHRPreferences {

    public static final String PREFS = "user_preferences";
    public static final String LANGUAGE = "language";

    public static Language getCurrentLanguage(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return Language.valueOf(sharedPref.getString(LANGUAGE, "EN"));
    }

    public static void setCurrentLanguage(Context context, String lang) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(LANGUAGE, lang);
        editor.apply();

        new BackupManager(context.getApplicationContext()).dataChanged();
    }
}
