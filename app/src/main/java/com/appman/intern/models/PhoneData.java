package com.appman.intern.models;

import android.database.Cursor;
import android.provider.ContactsContract;
import org.parceler.Parcel;
import lombok.Getter;
import lombok.Setter;

@Parcel
@Getter @Setter
public class PhoneData extends BaseContactModel {
    int phoneType;
    String phoneNo, phoneTypeName, normalizedNumber;

    public PhoneData(Cursor cursor) {
        id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
        contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
        rawContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
        lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY));
        phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        phoneType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
    }
}
