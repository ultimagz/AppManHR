package com.appman.intern.models;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class ContactData extends BaseContactModel {
    String displayName, photoId, photoUri, photoFileId, thumbnailUri, value, query;
    boolean inVisibleGroup, isUserProfile, hasPhoneNumber, isHeader;
    List<PhoneData> phoneList = new ArrayList<>();
    List<EmailData> emailList = new ArrayList<>();
    List<ImData> imList = new ArrayList<>();

    public ContactData() {}

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getPhotoFileId() {
        return photoFileId;
    }

    public void setPhotoFileId(String photoFileId) {
        this.photoFileId = photoFileId;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isInVisibleGroup() {
        return inVisibleGroup;
    }

    public void setInVisibleGroup(boolean inVisibleGroup) {
        this.inVisibleGroup = inVisibleGroup;
    }

    public boolean isUserProfile() {
        return isUserProfile;
    }

    public void setUserProfile(boolean userProfile) {
        isUserProfile = userProfile;
    }

    public boolean isHasPhoneNumber() {
        return hasPhoneNumber;
    }

    public void setHasPhoneNumber(boolean hasPhoneNumber) {
        this.hasPhoneNumber = hasPhoneNumber;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
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

    public List<ImData> getImList() {
        return new ArrayList<>(imList);
    }

    public void setImList(List<ImData> imList) {
        this.imList = new ArrayList<>(imList);
    }
}
