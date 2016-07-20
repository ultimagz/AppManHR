package com.appman.intern;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.appman.intern.models.AppContactData;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "DB_Contact";
    public static final String DBTABLE = "Contact";
    public static final int DBVERSION = 3;

    // Server contacts row
    public static final String CONTACT_ID = "contact_id";
    public static final String FIRST_NAME_TH = "firstName_th";
    public static final String LAST_NAME_TH = "lastName_th";
    public static final String NICK_NAME_TH = "nickName_th";
    public static final String FIRST_NAME_EN = "firstName_en";
    public static final String LAST_NAME_EN = "lastName_en";
    public static final String NICK_NAME_EN = "nickName_en";
    public static final String POSITION = "position";
    public static final String EMAIL = "email";
    public static final String MOBILE = "mobile";
    public static final String WORKPHONE = "workphone";
    public static final String LINE_ID = "line_id";
    public static final String UPDATE_TIME = "update_time";
    public static final String IMAGE = "image";

    // Local contacts row
    public static final String LOCAL_CONTACT_ID = "local_contact_id";
    public static final String LOCAL_FIRST_NAME_TH = "local_firstName_th";
    public static final String LOCAL_LAST_NAME_TH = "local_lastName_th";
    public static final String LOCAL_NICK_NAME_TH = "local_nickName_th";
    public static final String LOCAL_FIRST_NAME_EN = "local_firstName_en";
    public static final String LOCAL_LAST_NAME_EN = "local_lastName_en";
    public static final String LOCAL_NICK_NAME_EN = "local_nickName_en";
    public static final String LOCAL_POSITION = "local_position";
    public static final String LOCAL_EMAIL = "local_email";
    public static final String LOCAL_MOBILE = "local_mobile";
    public static final String LOCAL_WORKPHONE = "local_workphone";
    public static final String LOCAL_LINE_ID = "local_line";
    public static final String LOCAL_IMAGE = "local_image";
    public static final String LOCAL_UPDATE_TIME = "local_updateTime";

    public DatabaseHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("CREATE TABLE %s ( ", DBTABLE) +
                CONTACT_ID + " TEXT PRIMARY KEY, " +
                FIRST_NAME_TH + " TEXT, " +
                LAST_NAME_TH + " TEXT, " +
                NICK_NAME_TH + " TEXT, " +
                FIRST_NAME_EN + " TEXT, " +
                LAST_NAME_EN + " TEXT, " +
                NICK_NAME_EN + " TEXT, " +
                POSITION + " TEXT, " +
                EMAIL + " TEXT, " +
                MOBILE + " TEXT, " +
                WORKPHONE + " TEXT, " +
                LINE_ID + " TEXT, " +
                UPDATE_TIME + " TEXT," +
                IMAGE + " TEXT," +
                LOCAL_CONTACT_ID + " TEXT," +
                LOCAL_FIRST_NAME_TH + " TEXT, " +
                LOCAL_LAST_NAME_TH + " TEXT, " +
                LOCAL_NICK_NAME_TH + " TEXT, " +
                LOCAL_FIRST_NAME_EN + " TEXT, " +
                LOCAL_LAST_NAME_EN + " TEXT, " +
                LOCAL_NICK_NAME_EN + " TEXT, " +
                LOCAL_POSITION + " TEXT, " +
                LOCAL_EMAIL + " TEXT, " +
                LOCAL_MOBILE + " TEXT, " +
                LOCAL_WORKPHONE + " TEXT, " +
                LOCAL_LINE_ID + " TEXT, " +
                LOCAL_IMAGE + " TEXT, " +
                LOCAL_UPDATE_TIME + " TEXT)";

        db.execSQL(sql);
        Log.w("CREATE TABLE", "Create Table Successfully.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBTABLE);
        onCreate(db);
    }

    public List<AppContactData> getContactList() {
        SQLiteDatabase db = getReadableDatabase();
        List<AppContactData> contactList = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.DBTABLE, null, null, null, null, null, "fistName_EN ASC");

        if (cursor == null)
            return contactList;

        while (cursor.moveToNext()) {
            contactList.add(new AppContactData(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        Log.e("contactList", contactList.toString());

        return contactList;
    }

    public AppContactData getContactById(String id) {
        AppContactData contactData = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.DBTABLE, null, "CONTACT_ID = ?", new String[]{ id }, null, null, null);

        if (cursor != null) {
            contactData = new AppContactData(cursor);
            cursor.close();
        }

        db.close();

        return contactData;
    }

    public void insertContactList(List<AppContactData> contactList) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues insertValues;
        for (AppContactData contactData : contactList) {
            insertValues = contactData.createContentValues();
            db.insertWithOnConflict(DBTABLE, null, insertValues, SQLiteDatabase.CONFLICT_REPLACE);
        }

        db.close();
    }

    public long insertContactData(AppContactData contactData) {
        SQLiteDatabase db = getWritableDatabase();
        String position = contactData.getPosition().replace("'", " ''");
        ContentValues insertValues = contactData.createContentValues();
        long result = db.insertWithOnConflict(DBTABLE, null, insertValues, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        return result;
    }

    public int updateContactData(AppContactData contactData) {
        SQLiteDatabase db = getWritableDatabase();
        String where = CONTACT_ID + " = ?";
        String[] args = { contactData.getId() };
        ContentValues updateValues = contactData.createContentValues();
        int result = db.update(DatabaseHelper.DBTABLE, updateValues, where, args);
        db.close();

        return result;
    }

    public int deleteContactData(AppContactData contactData) {
        SQLiteDatabase db = getWritableDatabase();
        String where = CONTACT_ID + " = ?";
        String[] args = { contactData.getId() };

        int result = db.delete(DatabaseHelper.DBTABLE, where, args);
        db.close();

        return result;
    }
}
