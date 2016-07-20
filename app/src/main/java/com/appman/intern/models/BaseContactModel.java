package com.appman.intern.models;

import com.appman.intern.AppManHR;

import org.parceler.Parcel;

@Parcel
public class BaseContactModel {
    String id, contactId, rawContactId, lookupKey;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getRawContactId() {
        return rawContactId;
    }

    public void setRawContactId(String rawContactId) {
        this.rawContactId = rawContactId;
    }

    public String getLookupKey() {
        return lookupKey;
    }

    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
    }

    @Override
    public String toString() {
        return AppManHR.GSON_PRETTY.toJson(this);
    }
}
