package com.appman.intern.models;

import java.util.ArrayList;
import java.util.List;

public class ContactData extends BaseContactModel {
    String displayName, photoId, photoUri, photoFileId, thumbnailUri, value, query;
    boolean inVisibleGroup, isUserProfile, hasPhoneNumber, isHeader;
    List<PhoneData> phoneList = new ArrayList<>();
    List<EmailData> emailList = new ArrayList<>();

    public ContactData(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public ContactData(String value, boolean isHeader) {
        this.value = value;
        this.isHeader = isHeader;
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

    public String getLookupKey() {
        return lookupKey;
    }

    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
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

    public void setIsUserProfile(boolean userProfile) {
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
}
