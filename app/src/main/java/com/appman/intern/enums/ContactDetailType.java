package com.appman.intern.enums;

public enum ContactDetailType {
    MOBILE("Mobile"), WORK_PHONE("Workphone"), E_MAIL("E-Mail"), LINE("LINE");

    String mName;
    ContactDetailType(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
