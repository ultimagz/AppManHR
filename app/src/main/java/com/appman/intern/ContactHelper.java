package com.appman.intern;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.appman.intern.enums.Language;
import com.appman.intern.models.AppContactData;
import com.appman.intern.models.ContactData;
import com.appman.intern.models.EmailData;
import com.appman.intern.models.ImData;
import com.appman.intern.models.PhoneData;
import com.appman.intern.models.SearchableContactData;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import timber.log.Timber;

public class ContactHelper {

    public static RealmResults<AppContactData> getContactListFromDatabase(Context context) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AppContactData> query = realm.where(AppContactData.class);
        Language lang = AppManHRPreferences.getCurrentLanguage(context);
        String sortBy = lang == Language.TH ? "firstnameTh" : "firstnameEn";
        return query.findAllSorted(sortBy, Sort.ASCENDING);
    }

    public static List<SearchableContactData> getSearchableContactListFromDatabase(Context context) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AppContactData> query = realm.where(AppContactData.class);
        Language lang = AppManHRPreferences.getCurrentLanguage(context);
        String sortBy = lang == Language.TH ? "firstnameTh" : "firstnameEn";

        RealmResults<AppContactData> realmResults = query.findAllSorted(sortBy, Sort.ASCENDING);
        List<SearchableContactData> resultList = new ArrayList<>();
        for (AppContactData contactData : realmResults) {
            resultList.add(new SearchableContactData(contactData));
        }

        return resultList;
    }

    public static String getContactsJsonFromFile(Context context) {
        try {
            InputStream json = context.getAssets().open("sample_contact.json");
            return IOUtils.toString(json, "UTF-8");
        } catch (IOException e) {
            return("[]");
        }
    }

    public static String addNewContact(Context context, AppContactData newContact, Language lang) {
        try {
            String groupId = ContactHelper.getContactGroupId(context);
            ContentProviderResult[] results =
                    context.getContentResolver().applyBatch(
                            ContactsContract.AUTHORITY,
                            newContact.createNewContactProvider(lang, groupId));

//            Toast.makeText(context, "Insert contact success", Toast.LENGTH_SHORT).show();
//            for (ContentProviderResult result : results) {
//                Log.w("insert id", String.valueOf(result.uri));
//            }

            return results[0].uri.getLastPathSegment();

        } catch (RemoteException | OperationApplicationException e) {
            Log.e("Insert contact failed", String.valueOf(newContact), e);
            return null;
//            Toast.makeText(context, "Insert contact failed", Toast.LENGTH_SHORT).show();
        }
    }

    public static String updateContact(Context context, AppContactData updateContact, Language lang) {
        try {
            ContentProviderResult[] results =
                    context.getContentResolver().applyBatch(
                            ContactsContract.AUTHORITY,
                            updateContact.createUpdateContactProvider(lang));


//            Toast.makeText(context, "Insert contact success", Toast.LENGTH_SHORT).show();
//            for (ContentProviderResult result : results) {
//                Log.w("insert id", String.valueOf(result.uri));
//            }
//            Timber.w("update result %s", Arrays.toString(results));
            return Arrays.toString(results);

        } catch (RemoteException | OperationApplicationException e) {
            Timber.e(e, "Insert contact failed %s", String.valueOf(updateContact));
            return null;
//            Toast.makeText(context, "Insert contact failed", Toast.LENGTH_SHORT).show();
        }
    }

    public static List<ContactData> retrieveContacts(Context context, String[] projection, String groupId) { //This Context parameter is nothing but your Activity class's Context
        List<String> contactIdList = getContactIdsInGroup(context, groupId);
        List<ContactData> contactDataList = new ArrayList<>();
        if (contactIdList.size() == 0) {
            return contactDataList;
        }

        String selection =
                ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1' AND " +
                ContactsContract.Contacts._ID + " IN (" + TextUtils.join(", ", contactIdList) + ")";
        String[] args = {};

        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, selection, args, null);
        if (cursor == null) {
            return contactDataList;
        }

        while(cursor.moveToNext()) {
            ContactData contactData = new ContactData(cursor);

            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            contactData.setRawContactId(retrieveRawContactId(context, id));
            contactData.setImList(retrieveImList(context, id));
            contactData.setEmailList(retrieveEmailList(context, id));

            if (contactData.isHasPhoneNumber()) {
                contactData.setPhoneList(retrievePhoneList(context, id));
            }

            contactDataList.add(contactData);
        }

        cursor.close();

        Timber.w("Contacts %s", String.valueOf(contactDataList));
        return contactDataList;
    }

    private static List<String> getContactIdsInGroup(Context context, String groupId) {
        List<String> contactIdList = new ArrayList<>();
        String[] projection = new String[] {
                ContactsContract.Data._ID,
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID
        };

        String selection = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + " = ? ";
        String[] args = { groupId };

        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, selection, args, null);
        if (cursor == null) {
            return contactIdList;
        }

        while(cursor.moveToNext()) {
            contactIdList.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
        }

        cursor.close();
        Timber.w("ContactIds in Group[%s] %s", groupId, String.valueOf(contactIdList));
        return contactIdList;
    }

    private static String retrieveRawContactId(Context context, String contactId) {
        Cursor cursor = context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                null, ContactsContract.RawContacts.CONTACT_ID + " = ?", new String[] { contactId }, null);

        if (cursor == null) {
            return null;
        }

        cursor.moveToFirst();
        String rawContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
        cursor.close();

        return rawContactId;
    }

    private static List<EmailData> retrieveEmailList(Context context, String contactId) {
        List<EmailData> emailList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{ contactId }, null);

        if (cursor == null) {
            return emailList;
        }

        while (cursor.moveToNext()) {
            emailList.add(new EmailData(cursor));
        }

        cursor.close();

        Timber.w("Email List %s", String.valueOf(emailList));
        return emailList;
    }

    private static List<PhoneData> retrievePhoneList(Context context, String contactId) {
        List<PhoneData> phoneList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                new String[]{ contactId }, null);
//        Log.w("phone column(s)", Arrays.toString(cursor.getColumnNames()));

        if (cursor == null)
            return phoneList;

        while (cursor.moveToNext()) {
            phoneList.add(new PhoneData(cursor));
        }

        cursor.close();

        Timber.w("Phone Model %s", String.valueOf(phoneList));
        return phoneList;
    }

    private static List<ImData> retrieveImList(Context context, String contactId) {
        List<ImData> imList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ? AND " + ContactsContract.Data.MIMETYPE + "=?",
                new String[]{ contactId, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE }, null);
//        Log.w("phone column(s)", Arrays.toString(cursor.getColumnNames()));

        if (cursor == null)
            return imList;

        while (cursor.moveToNext()) {
            imList.add(new ImData(cursor));
        }

        cursor.close();

        Timber.w("IM Model %s", String.valueOf(imList));
        return imList;
    }

    public static String getContactGroupId(Context context) {
        String groupId = checkExistGroup(context, Utils.GROUP_NAME);

        if (groupId == null) {
            ArrayList<ContentProviderOperation> opsGroup = new ArrayList<>();
            opsGroup.add(ContentProviderOperation.newInsert(ContactsContract.Groups.CONTENT_URI)
                    .withValue(ContactsContract.Groups.TITLE, Utils.GROUP_NAME)
                    .withValue(ContactsContract.Groups.GROUP_VISIBLE, true)
                    .withValue(ContactsContract.Groups.GROUP_IS_READ_ONLY, true)
                    .withValue(ContactsContract.Groups.ACCOUNT_NAME, Utils.ACCOUNT_NAME)
                    .withValue(ContactsContract.Groups.ACCOUNT_TYPE, Utils.ACCOUNT_TYPE)
                    .build());

            try {
                ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, opsGroup);
                groupId = results[0].uri.getLastPathSegment();
                Timber.w("create group result %s", groupId);
            } catch (RemoteException | OperationApplicationException e) {
                Timber.e(e, "create group failed %s", Utils.GROUP_NAME);
            }

            return groupId;
        } else {
            return groupId;
        }
    }

    private static String checkExistGroup(Context context, String groupName) {
        String selection = ContactsContract.Groups.DELETED + " = ? AND " + ContactsContract.Groups.GROUP_VISIBLE + " = ? AND " + ContactsContract.Groups.TITLE + " = ?";
        String[] selectionArgs = { "0", "1", groupName };
        Cursor cursor = context.getContentResolver().query(ContactsContract.Groups.CONTENT_URI, null, selection, selectionArgs, null);

        if (cursor == null)
            return null;

        String id = null;
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            id = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups._ID));
        }

        cursor.close();

        return id;
    }
}
