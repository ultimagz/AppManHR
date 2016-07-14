package com.appman.intern;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "DB_Contact";
    public static final String DBTABLE = "Contact";
    public static final int DBVERSION = 1;

    // Server contacts row
    public static final String contactID = "contact_id";
    public static final String fistNameTH = "fistName_th";
    public static final String lastNameTH = "lastName_th";
    public static final String nickNameTH = "nickName_th";
    public static final String fistNameEN = "fistName_en";
    public static final String lastNameEN = "lastName_en";
    public static final String nickNameEN = "nickName_en";
    public static final String position = "position";
    public static final String email = "email";
    public static final String mobile = "mobile";
    public static final String workphone = "workphone";
    public static final String lineID = "line_id";
    public static final String updateTime = "updateTime";

    // Local contacts row
    public static final String localContactID = "local_contact_id";
    public static final String localFistNameTH = "local_fistName_th";
    public static final String localLastNameTH = "local_lastName_th";
    public static final String localNickNameTH = "local_nickName_th";
    public static final String localFistNameEN = "local_fistName_en";
    public static final String localLastNameEN = "local_lastName_en";
    public static final String localNickNameEN = "local_nickName_en";
    public static final String localPosition = "local_position";
    public static final String localEmail = "local_email";
    public static final String localMobile = "local_mobile";
    public static final String localWorkphone = "local_workphone";
    public static final String localLineID = "local_line";
    public static final String localUpdateTime = "local_updateTime";

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
                lineID + " TEXT, " +
                updateTime + " TEXT," +
                localContactID + " TEXT," +
                localFistNameTH + " TEXT, " +
                localLastNameTH + " TEXT, " +
                localNickNameTH + " TEXT, " +
                localFistNameEN + " TEXT, " +
                localLastNameEN + " TEXT, " +
                localNickNameEN + " TEXT, " +
                localPosition + " TEXT, " +
                localEmail + " TEXT, " +
                localMobile + " TEXT, " +
                localWorkphone + " TEXT, " +
                localLineID + " TEXT, " +
                localUpdateTime + " TEXT)";

        db.execSQL(sql);
        Log.w("CREATE TABLE", "Create Table Successfully.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
