package com.appman.intern.models;

import com.appman.intern.enums.ContactDetailType;

public class ContactDetailRowModel {
    ContactDetailType viewType;
    String data;

    public ContactDetailRowModel(String data, ContactDetailType viewType) {
        this.data = data;
        this.viewType = viewType;
    }

    public ContactDetailType getViewType() {
        return viewType;
    }

    public String getData() {
        return data;
    }

    public String getType() {
        return viewType.getName();
    }
}
