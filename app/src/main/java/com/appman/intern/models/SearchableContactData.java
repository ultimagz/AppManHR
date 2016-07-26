package com.appman.intern.models;

import android.text.TextUtils;

import org.parceler.Parcel;

@Parcel
public class SearchableContactData {
    String id = "id";
    String firstnameTh = "firstnameTh";
    String lastnameTh = "lastnameTh";
    String nicknameTh = "nicknameTh";
    String firstnameEn = "firstnameEn";
    String lastnameEn = "lastnameEn";
    String nicknameEn = "nicknameEn";
    String workPhone = "021234567";
    String lineID = "line_id";
    String updateTime = "UPDATE_TIME";
    String image = "image";
    String position = "position";
    String mobile = "0897654321";
    String email = "e@mail.com";
    boolean isHeader = false;

    public SearchableContactData() {}

    public SearchableContactData(AppContactData contactData) {
        id = contactData.getId();
        firstnameTh = contactData.getFirstnameTh();
        lastnameTh = contactData.getLastnameTh();
        nicknameTh = contactData.getNicknameTh();
        firstnameEn = contactData.getFirstnameEn();
        lastnameEn = contactData.getLastnameEn();
        nicknameEn = contactData.getNicknameEn();
        position = contactData.getPosition();
        email = contactData.getEmail();
        mobile = contactData.getMobile();
        workPhone = contactData.getWorkPhone();
        lineID = contactData.getLineID();
        updateTime = contactData.getUpdateTime();
        image = contactData.getImage();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstnameTh() {
        return firstnameTh;
    }

    public void setFirstnameTh(String firstnameTh) {
        this.firstnameTh = firstnameTh;
    }

    public String getLastnameTh() {
        return lastnameTh;
    }

    public void setLastnameTh(String lastnameTh) {
        this.lastnameTh = lastnameTh;
    }

    public String getNicknameTh() {
        return nicknameTh;
    }

    public void setNicknameTh(String nicknameTh) {
        this.nicknameTh = nicknameTh;
    }

    public String getFirstnameEn() {
        return firstnameEn;
    }

    public void setFirstnameEn(String firstnameEn) {
        this.firstnameEn = firstnameEn;
    }

    public String getLastnameEn() {
        return lastnameEn;
    }

    public void setLastnameEn(String lastnameEn) {
        this.lastnameEn = lastnameEn;
    }

    public String getNicknameEn() {
        return nicknameEn;
    }

    public void setNicknameEn(String nicknameEn) {
        this.nicknameEn = nicknameEn;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getLineID() {
        return lineID;
    }

    public void setLineID(String lineID) {
        this.lineID = lineID;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public String getFirstCharTh() {
        return firstnameTh.substring(0, 1).toUpperCase();
    }

    public String getFirstCharEn() {
        return firstnameEn.substring(0, 1).toUpperCase();
    }

    public String getAllNameTh() {
        if (TextUtils.isEmpty(firstnameTh) && TextUtils.isEmpty(lastnameTh)) {
            return getAllNameEn();
        } else if (TextUtils.isEmpty(nicknameTh)) {
            return String.format("%s %s", firstnameTh, lastnameTh);
        } else {
            return String.format("%s %s (%s)", firstnameTh, lastnameTh, nicknameTh);
        }
    }

    public String getAllNameEn() {
        if (TextUtils.isEmpty(firstnameEn) && TextUtils.isEmpty(lastnameEn)) {
            return getAllNameTh();
        } else if (TextUtils.isEmpty(nicknameEn)) {
            return String.format("%s %s", firstnameEn, lastnameEn);
        } else {
            return String.format("%s %s (%s)", firstnameEn, lastnameEn, nicknameEn);
        }
    }

    public boolean regionMatches(String filterString) {
        int searchLength = filterString.length();
        boolean match = false;

        if (!TextUtils.isEmpty(firstnameEn))
            match |= firstnameEn.regionMatches(true, 0, filterString, 0, searchLength);

        if (!TextUtils.isEmpty(lastnameEn))
            match |= lastnameEn.regionMatches(true, 0, filterString, 0, searchLength);

        if (!TextUtils.isEmpty(nicknameEn))
            match |= nicknameEn.regionMatches(true, 0, filterString, 0, searchLength);

        if (!TextUtils.isEmpty(firstnameTh))
            match |= firstnameTh.regionMatches(true, 0, filterString, 0, searchLength);

        if (!TextUtils.isEmpty(lastnameTh))
            match |= lastnameTh.regionMatches(true, 0, filterString, 0, searchLength);

        if (!TextUtils.isEmpty(nicknameTh))
            match |= nicknameTh.regionMatches(true, 0, filterString, 0, searchLength);

        if (!TextUtils.isEmpty(position))
            match |= position.regionMatches(true, 0, filterString, 0, searchLength);

        return match;
    }
}
