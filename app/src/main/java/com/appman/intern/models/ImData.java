package com.appman.intern.models;

import android.database.Cursor;
import android.provider.ContactsContract;

import org.parceler.Parcel;

@Parcel
public class ImData extends BaseContactModel {

    int imType;
    String protocol, customProtocol, label, data;

    public ImData() {}

    public ImData(Cursor cursor) {
        id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im._ID));
        contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.CONTACT_ID));
        rawContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.RAW_CONTACT_ID));
        lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.LOOKUP_KEY));
        imType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
        protocol = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));
        customProtocol = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL));
        label = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.LABEL));
        data = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
    }

    public int getImType() {
        return imType;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getCustomProtocol() {
        return customProtocol;
    }

    public String getLabel() {
        return label;
    }

    public String getData() {
        return data;
    }
}
