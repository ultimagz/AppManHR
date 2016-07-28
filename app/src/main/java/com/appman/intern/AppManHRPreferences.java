package com.appman.intern;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.appman.intern.enums.Language;

public class AppManHRPreferences {

    public static final String PREFS = "user_preferences";
    public static final String LOG_IN = "log_in";
    public static final String LANGUAGE = "language";
    public static final String EXPORT_TIME = "export_time";

    public static void setLogin(Context context, boolean isLogin) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(LOG_IN, isLogin);
        editor.apply();
    }

    public static boolean isLogin(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(LOG_IN, false);
    }

    public static String getLastExportTime(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(EXPORT_TIME, "");
    }

    public static void setLastExportTime(Context context, String exportTime) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(EXPORT_TIME, exportTime);
        editor.apply();
    }

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
