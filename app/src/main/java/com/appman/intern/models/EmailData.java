package com.appman.intern.models;

import android.database.Cursor;
import android.provider.ContactsContract;

public class EmailData extends BaseContactModel {
    int emailType;
    String emailAddress, emailTypeName;

    public EmailData(Cursor cursor) {
        id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email._ID));
        contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID));
        rawContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID));
        lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.LOOKUP_KEY));
        emailAddress = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
        emailType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
    }

    public int getEmailType() {
        return emailType;
    }

    public void setEmailType(int emailType) {
        this.emailType = emailType;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailTypeName() {
        return emailTypeName;
    }

    public void setEmailTypeName(String emailTypeName) {
        this.emailTypeName = emailTypeName;
    }
}
