package com.appman.intern.models;

import com.appman.intern.AppManHR;

import org.parceler.Parcel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Parcel
@Getter @Setter
@NoArgsConstructor
public class BaseContactModel {
    String id, contactId, rawContactId, lookupKey;

    @Override
    public String toString() {
        return AppManHR.GSON_PRETTY.toJson(this);
    }
}
