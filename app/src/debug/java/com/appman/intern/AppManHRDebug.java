package com.appman.intern;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

public class AppManHRDebug extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                RealmInspectorModulesProvider.builder(this)
                                        .databaseNamePattern(Pattern.compile(".+\\.realm"))
                                        .build())
                        .build());

        RealmConfiguration realmConfiguration =
                new RealmConfiguration.Builder(this)
                        .deleteRealmIfMigrationNeeded()
                        .name("hr_contact.realm")
                        .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        Timber.plant(new Timber.DebugTree());
    }
}
