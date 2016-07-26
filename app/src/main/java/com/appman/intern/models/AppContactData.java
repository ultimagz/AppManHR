package com.appman.intern.models;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Base64;

import com.appman.intern.DatabaseHelper;
import com.appman.intern.Utils;
import com.appman.intern.enums.Language;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import timber.log.Timber;

@Parcel
public class AppContactData extends RealmObject {

    @SerializedName("_id")
    @PrimaryKey
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
    String updateTime = "UPDATE_TIME";

    @SerializedName("avatarbase64")
    String image = "image";

    String position = "position";
    String mobile = "0897654321";
    String email = "e@mail.com";

    boolean isHeader = false;
    boolean exported = false;

    LocalContactData localContactData;

    public AppContactData() {}

    public AppContactData(Cursor cursor) {
        id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONTACT_ID));
        firstnameTh = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIRST_NAME_TH));
        lastnameTh = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAST_NAME_TH));
        nicknameTh = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NICK_NAME_TH));
        firstnameEn = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIRST_NAME_EN));
        lastnameEn = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAST_NAME_EN));
        nicknameEn = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NICK_NAME_EN));
        position = cursor.getString(cursor.getColumnIndex(DatabaseHelper.POSITION));
        email = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EMAIL));
        mobile = cursor.getString(cursor.getColumnIndex(DatabaseHelper.MOBILE));
        workPhone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.WORKPHONE));
        lineID = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LINE_ID));
        updateTime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UPDATE_TIME));
        image = cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMAGE));
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

    public boolean isExported() {
        return exported;
    }

    public void setExported(boolean exported) {
        this.exported = exported;
    }

    public LocalContactData getLocalContactData() {
        return localContactData;
    }

    public void setLocalContactData(LocalContactData localContactData) {
        this.localContactData = localContactData;
    }

    public String getFullNameTh() {
        return TextUtils.join(" ", new String[]{ firstnameTh, lastnameTh });
    }

    public String getFullNameEn() {
        return TextUtils.join(" ", new String[]{ firstnameEn, lastnameEn });
    }

    public String getAllNameTh() {
        return String.format("%s %s (%s)", firstnameTh, lastnameTh, nicknameTh);
    }

    public String getAllNameEn() {
        return String.format("%s %s (%s)", firstnameEn, lastnameEn, nicknameEn);
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
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, Utils.ACCOUNT_TYPE)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, Utils.ACCOUNT_NAME)
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
        if (!TextUtils.isEmpty(mobile))
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobile)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());

        // WORK PHONE
        if (!TextUtils.isEmpty(workPhone))
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, workPhone)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                    .build());

        // EMAIL
        if (!TextUtils.isEmpty(email))
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());

        // LINE
        if (!TextUtils.isEmpty(lineID))
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Im.LABEL, "LINE")
                    .withValue(ContactsContract.CommonDataKinds.Im.TYPE, ContactsContract.CommonDataKinds.Im.TYPE_CUSTOM)
                    .withValue(ContactsContract.CommonDataKinds.Im.PROTOCOL, ContactsContract.CommonDataKinds.Im.PROTOCOL_CUSTOM)
                    .withValue(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL, "LINE")
                    .withValue(ContactsContract.CommonDataKinds.Im.DATA, lineID)
                    .build());

        // IMAGE
        if (!TextUtils.isEmpty(image)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] decodedBytes = Base64.decode(image, Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bitmapBytes = baos.toByteArray();

            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, bitmapBytes)
                    .build());
        }

        return ops;
    }

    public ArrayList<ContentProviderOperation> createUpdateContactProvider(Language lang) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        boolean isThai = lang == Language.TH;

        // NAME
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?", new String[]{ localContactData.getLocalId(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE })
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, isThai ? getFullNameTh() : getFullNameEn())
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, isThai ? lastnameTh : lastnameEn)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, isThai ? firstnameTh : firstnameEn)
                .build());

        // NICKNAME
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.CommonDataKinds.Nickname.TYPE + "=?", new String[]{ localContactData.getLocalId(), ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE, String.valueOf(ContactsContract.CommonDataKinds.Nickname.TYPE_CUSTOM) })
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Nickname.NAME, isThai ? nicknameTh : nicknameEn)
                .withValue(ContactsContract.CommonDataKinds.Nickname.TYPE, ContactsContract.CommonDataKinds.Nickname.TYPE_CUSTOM)
                .build());

        // MOBILE
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.CommonDataKinds.Phone.TYPE + "=?", new String[]{ localContactData.getLocalId(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) })
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobile)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        // WORK PHONE
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.CommonDataKinds.Phone.TYPE + "=?", new String[]{ localContactData.getLocalId(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK) })
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, workPhone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .build());

        // EMAIL
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.CommonDataKinds.Email.TYPE + "=?", new String[]{ localContactData.getLocalId(), ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_WORK) })
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());

        // LINE
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(
                        ContactsContract.Data.RAW_CONTACT_ID + "=? AND " +
                        ContactsContract.Data.MIMETYPE + "=? AND " +
                        ContactsContract.CommonDataKinds.Im.TYPE + "=? AND " +
                        ContactsContract.CommonDataKinds.Im.PROTOCOL + "=? AND " +
                        ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL + "=? AND " +
                        ContactsContract.CommonDataKinds.Im.LABEL + "=?",
                        new String[]{
                                localContactData.getLocalId(),
                                ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE,
                                String.valueOf(ContactsContract.CommonDataKinds.Im.TYPE_CUSTOM),
                                String.valueOf(ContactsContract.CommonDataKinds.Im.PROTOCOL_CUSTOM),
                                "LINE", "LINE" })
                .withValue(ContactsContract.CommonDataKinds.Im.TYPE, ContactsContract.CommonDataKinds.Im.TYPE_CUSTOM)
                .withValue(ContactsContract.CommonDataKinds.Im.PROTOCOL, ContactsContract.CommonDataKinds.Im.PROTOCOL_CUSTOM)
                .withValue(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL, "LINE")
                .withValue(ContactsContract.CommonDataKinds.Im.LABEL, "LINE")
                .withValue(ContactsContract.CommonDataKinds.Im.DATA, lineID)
                .build());

        // IMAGE
        if (!TextUtils.isEmpty(image)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] decodedBytes = Base64.decode(image, Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bitmapBytes = baos.toByteArray();

            Timber.w("Image %s size %d", getFullNameEn(), bitmapBytes.length);

            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(
                            ContactsContract.Data.RAW_CONTACT_ID + "=? AND " +
                            ContactsContract.Data.MIMETYPE + "=?",
                            new String[]{
                                    localContactData.getLocalId(),
                                    ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE})
                    .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, bitmapBytes)
                    .build());
        }

        return ops;
    }

    public ContentValues createContentValues() {
        String position = this.position.replace("'", "''");
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.CONTACT_ID, id);
        contentValues.put(DatabaseHelper.FIRST_NAME_TH, firstnameTh);
        contentValues.put(DatabaseHelper.LAST_NAME_TH, lastnameTh);
        contentValues.put(DatabaseHelper.NICK_NAME_TH, nicknameTh);
        contentValues.put(DatabaseHelper.FIRST_NAME_EN, firstnameEn);
        contentValues.put(DatabaseHelper.LAST_NAME_EN, lastnameEn);
        contentValues.put(DatabaseHelper.NICK_NAME_EN, nicknameEn);
        contentValues.put(DatabaseHelper.POSITION, position);
        contentValues.put(DatabaseHelper.EMAIL, email);
        contentValues.put(DatabaseHelper.MOBILE, mobile);
        contentValues.put(DatabaseHelper.WORKPHONE, workPhone);
        contentValues.put(DatabaseHelper.LINE_ID, lineID);
        contentValues.put(DatabaseHelper.UPDATE_TIME, updateTime);
        contentValues.put(DatabaseHelper.IMAGE, image);

        contentValues.put(DatabaseHelper.LOCAL_CONTACT_ID, id);
        contentValues.put(DatabaseHelper.LOCAL_FIRST_NAME_TH, firstnameTh);
        contentValues.put(DatabaseHelper.LOCAL_LAST_NAME_TH, lastnameTh);
        contentValues.put(DatabaseHelper.LOCAL_NICK_NAME_TH, nicknameTh);
        contentValues.put(DatabaseHelper.LOCAL_FIRST_NAME_EN, firstnameEn);
        contentValues.put(DatabaseHelper.LOCAL_LAST_NAME_EN, lastnameEn);
        contentValues.put(DatabaseHelper.LOCAL_NICK_NAME_EN, nicknameEn);
        contentValues.put(DatabaseHelper.LOCAL_POSITION, position);
        contentValues.put(DatabaseHelper.LOCAL_EMAIL, email);
        contentValues.put(DatabaseHelper.LOCAL_MOBILE, mobile);
        contentValues.put(DatabaseHelper.LOCAL_WORKPHONE, workPhone);
        contentValues.put(DatabaseHelper.LOCAL_LINE_ID, lineID);
        contentValues.put(DatabaseHelper.LOCAL_UPDATE_TIME, updateTime);
        contentValues.put(DatabaseHelper.LOCAL_IMAGE, image);

        return contentValues;
    }

    @Override
    public String toString() {
        return Utils.GSON_PRETTY.toJson(this);
    }

    public static Comparator<AppContactData> getComparator(Language lang) {
        if (lang == Language.TH)
            return new Comparator<AppContactData>() {
                @Override
                public int compare(AppContactData s1, AppContactData s2) {
                    return s1.getFirstnameTh().compareToIgnoreCase(s2.getFirstnameTh());
                }
            };

        return new Comparator<AppContactData>() {
            @Override
            public int compare(AppContactData s1, AppContactData s2) {
                return s1.getFirstnameEn().compareToIgnoreCase(s2.getFirstnameEn());
            }
        };
    }
}
