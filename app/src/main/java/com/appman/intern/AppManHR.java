package com.appman.intern;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

public class AppManHR extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.setDefaultConfiguration(
                new RealmConfiguration.Builder(this)
                        .deleteRealmIfMigrationNeeded()
                        .name("hr_contact.realm")
                        .build());
        Timber.plant(new CrashReportingTree());
    }
}
