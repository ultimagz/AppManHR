package com.appman.intern.models;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Parcel
@Getter @Setter
public class ContactData extends BaseContactModel {
    String displayName, photoId, photoUri, photoFileId, thumbnailUri, value, query;
    boolean inVisibleGroup, isUserProfile, hasPhoneNumber, isHeader;
    List<PhoneData> phoneList = new ArrayList<>();
    List<EmailData> emailList = new ArrayList<>();

    public ContactData(Cursor cursor) {
        id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        thumbnailUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
        photoId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
        photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
        photoFileId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_FILE_ID));
        isUserProfile = TextUtils.equals(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.IS_USER_PROFILE)), "1");
        inVisibleGroup = TextUtils.equals(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.IN_VISIBLE_GROUP)), "1");
        hasPhoneNumber = TextUtils.equals(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)), "1");
        lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
    }

    public List<PhoneData> getPhoneList() {
        return new ArrayList<>(phoneList);
    }

    public void setPhoneList(List<PhoneData> phoneList) {
        this.phoneList = new ArrayList<>(phoneList);
    }

    public List<EmailData> getEmailList() {
        return new ArrayList<>(emailList);
    }

    public void setEmailList(List<EmailData> emailList) {
        this.emailList = new ArrayList<>(emailList);
    }
}
