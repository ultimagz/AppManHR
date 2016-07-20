package com.appman.intern.models;

import org.parceler.Parcel;

import lombok.Getter;
import lombok.Setter;

@Parcel
@Getter @Setter
public class DataModel extends BaseContactModel {
    String dataId;

    public DataModel(String rawContactId, String dataId) {
        this.rawContactId = rawContactId;
        this.dataId = dataId;
    }
}
