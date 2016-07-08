package com.appman.intern.models;

public class DataModel extends BaseContactModel {
    String dataId;

    public DataModel(String rawContactId, String dataId) {
        this.rawContactId = rawContactId;
        this.dataId = dataId;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }
}
