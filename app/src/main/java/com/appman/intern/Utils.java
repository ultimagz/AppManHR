package com.appman.intern;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utils {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    public static final Gson GSON = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
    public static final Gson GSON_PRETTY = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();
    public static final String URL = "http://hr.appmanproject.com/api/user/list";
    public static final String GROUP_NAME = "AppManHR";
    public static final String ACCOUNT_TYPE = "";
    public static final String ACCOUNT_NAME = "";

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
