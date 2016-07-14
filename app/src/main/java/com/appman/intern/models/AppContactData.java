package com.appman.intern.models;

import android.content.ContentProviderOperation;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.appman.intern.AppManHR;
import com.appman.intern.enums.Language;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Comparator;

@Parcel
public class AppContactData {

    @SerializedName("_id")
    String id = "id";
    @SerializedName("th_fname")
    String firstnameTh = "firstnameTh";
    @SerializedName("th_lname")
    String lastnameTh = "lastnameTh";
    @SerializedName("th_nickname")
    String nicknameTh = "nicknameTh";
    @SerializedName("en_fname")
    String firstnameEn = "firstnameEn";
    @SerializedName("en_lname")
    String lastnameEn = "lastnameEn";
    @SerializedName("en_nickname")
    String nicknameEn = "nicknameEn";
    @SerializedName("workphone")
    String workPhone = "021234567";
    @SerializedName("line")
    String lineID = "line_id";
    @SerializedName("update")
    String update = "updateTime";

    @SerializedName("avatarbase64")
    String image = "image";




    String position = "position";
    String mobile = "0897654321";
    String email = "e@mail.com";

    boolean isHeader = false;

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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLineID() {
        return lineID;
    }

    public void setLineID(String lineID) {
        this.lineID = lineID;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public boolean isHeader() {
        return isHeader;
    }

    public void setIsHeader(boolean header) {
        isHeader = header;
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

    public ArrayList<ContentProviderOperation> createNewContactProvider(Language lang, String groupId) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        boolean isThai = lang == Language.TH;

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withYieldAllowed(true)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, AppManHR.ACCOUNT_TYPE)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, AppManHR.ACCOUNT_NAME)
                .build());

        // GROUP
        if (!TextUtils.isEmpty(groupId)) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, groupId)
                    .build());
        }

        // NAME
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, isThai ? getFullNameTh() : getFullNameEn())
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, isThai ? lastnameTh : lastnameEn)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, isThai ? firstnameTh : firstnameEn)
                .build());

        // NICKNAME
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Nickname.NAME, isThai ? nicknameTh : nicknameEn)
                .withValue(ContactsContract.CommonDataKinds.Nickname.TYPE, ContactsContract.CommonDataKinds.Nickname.TYPE_CUSTOM)
                .build());

        // MOBILE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobile)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        // WORK PHONE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, workPhone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .build());

        // EMAIL
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());

        return ops;
    }

    @Override
    public String toString() {
        return AppManHR.GSON_PRETTY.toJson(this);
    }

    public static Comparator<AppContactData> getComparator(Language lang) {
        if (lang == Language.TH)
            return new Comparator<AppContactData>() {
                @Override
                public int compare(AppContactData s1, AppContactData s2) {
                    return s1.getFirstCharTh().compareToIgnoreCase(s2.getFirstCharTh());
                }
            };

        return new Comparator<AppContactData>() {
            @Override
            public int compare(AppContactData s1, AppContactData s2) {
                return s1.getFirstCharEn().compareToIgnoreCase(s2.getFirstCharEn());
            }
        };
    }
}
