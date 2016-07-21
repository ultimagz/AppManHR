package com.appman.intern;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;

import java.io.IOException;

import timber.log.Timber;

public class AppBackupAgentHelper extends BackupAgentHelper {

    public static final String PREFS_BACKUP = "prefs";
    public static final String DB_BACKUP = "db";
    @Override
    public void onCreate() {
        super.onCreate();
        FileBackupHelper dbBackupHelper = new FileBackupHelper(this, "../databases/" + DatabaseHelper.DBNAME);
        SharedPreferencesBackupHelper prefsBackupHelper = new SharedPreferencesBackupHelper(this, AppManHRPreferences.PREFS);
        addHelper(DB_BACKUP, dbBackupHelper);
        addHelper(PREFS_BACKUP, prefsBackupHelper);
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        super.onRestore(data, appVersionCode, newState);
        Timber.w("AppBackupAgentHelper onRestore");
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        super.onBackup(oldState, data, newState);
        Timber.w("AppBackupAgentHelper onBackup");
    }
}
