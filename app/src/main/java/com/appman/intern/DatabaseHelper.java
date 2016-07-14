package com.appman.intern;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "DB_Contact";
    public static final String DBTABLE = "Contact";
    public static final int DBVERSION = 1;


    public static final String contactID = "contact_id";
    public static final String fistNameTH = "fistName_TH";
    public static final String lastNameTH = "lastName_TH";
    public static final String nickNameTH = "nickName_TH";
    public static final String fistNameEN = "fistName_EN";
    public static final String lastNameEN = "lastName_EN";
    public static final String nickNameEN = "nickName_EN";
    public static final String position = "position";
    public static final String email = "email";
    public static final String mobile = "mobile";
    public static final String workphone = "workphone";
    public static final String line = "line";
    public static final String updateTime = "updateTime";

    public DatabaseHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("CREATE TABLE %s ( ", DBTABLE) +
                contactID + " TEXT PRIMARY KEY, " +
                fistNameTH + " TEXT, " +
                lastNameTH + " TEXT, " +
                nickNameTH + " TEXT, " +
                fistNameEN + " TEXT, " +
                lastNameEN + " TEXT, " +
                nickNameEN + " TEXT, " +
                position + " TEXT, " +
                email + " TEXT, " +
                mobile + " TEXT, " +
                workphone + " TEXT, " +
                line + " TEXT, " +
                updateTime + " TEXT)";

        db.execSQL(sql);
        Log.w("CREATE TABLE", "Create Table Successfully.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBTABLE);
        onCreate(db);

    }
}
