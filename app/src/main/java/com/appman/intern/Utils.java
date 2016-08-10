package com.appman.intern;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.OkHttpClient;

public class Utils {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    public static final Gson GSON = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
    public static final Gson GSON_PRETTY = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();
    public static final String URL = "http://hr.appmanproject.com/api/user/list";
    public static final String GROUP_NAME = "AppManHR";
    public static final String ACCOUNT_TYPE = "";
    public static final String ACCOUNT_NAME = "";
    public static final  OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void hideSoftKeyboard(Activity activity) {
        View focusView = activity.getCurrentFocus();
        if (focusView != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
    }
}
