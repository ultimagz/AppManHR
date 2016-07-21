package com.appman.intern.models;

import android.text.TextUtils;

import com.appman.intern.Utils;
import com.appman.intern.enums.Language;

import org.parceler.Parcel;

import java.util.Comparator;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel
public class LocalContactData extends RealmObject {
    @PrimaryKey
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

    public LocalContactData() {}

    public LocalContactData(AppContactData contactData) {
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

    public String getFullNameTh() {
        return TextUtils.join(" ", new String[]{ firstnameTh, lastnameTh });
    }

    public String getFullNameEn() {
        return TextUtils.join(" ", new String[]{ firstnameEn, lastnameEn });
    }

    public String getFirstCharTh() {
        return firstnameTh.substring(0, 1).toUpperCase();
    }

    public String getFirstCharEn() {
        return firstnameEn.substring(0, 1).toUpperCase();
    }

    @Override
    public String toString() {
        return Utils.GSON_PRETTY.toJson(this);
    }

    public static Comparator<LocalContactData> getComparator(Language lang) {
        if (lang == Language.TH)
            return new Comparator<LocalContactData>() {
                @Override
                public int compare(LocalContactData s1, LocalContactData s2) {
                    return s1.getFirstnameTh().compareToIgnoreCase(s2.getFirstnameTh());
                }
            };

        return new Comparator<LocalContactData>() {
            @Override
            public int compare(LocalContactData s1, LocalContactData s2) {
                return s1.getFirstnameEn().compareToIgnoreCase(s2.getFirstnameEn());
            }
        };
    }
}
