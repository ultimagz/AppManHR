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
import android.view.View;
import android.widget.Toast;

import com.appman.intern.enums.Language;
import com.appman.intern.models.AppContactData;
import com.appman.intern.models.ContactData;
import com.appman.intern.models.EmailData;
import com.appman.intern.models.PhoneData;

import java.util.ArrayList;
import java.util.List;

public class ContactHelper {

    public void addNewContact(Context context, View view) {
        AppContactData newContact = new AppContactData();
        try {
            String groupId = ContactHelper.getContactGroupId(context);
            Language lang = AppManHRPreferences.getCurrentLanguage(context);
            ContentProviderResult[] results =
                    context.getContentResolver().applyBatch(
                            ContactsContract.AUTHORITY,
                            newContact.createNewContactProvider(lang, groupId));

            Toast.makeText(context, "Insert contact success", Toast.LENGTH_SHORT).show();

            for (ContentProviderResult result : results) {
                Log.w("insert id", String.valueOf(result.uri));
            }

        } catch (RemoteException | OperationApplicationException e) {
            Log.e("Insert contact failed", String.valueOf(newContact), e);
            Toast.makeText(context, "Insert contact failed", Toast.LENGTH_SHORT).show();
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
            contactData.setEmailList(retrieveEmailList(context, contactData.getId()));
            contactData.setRawContactId(retrieveRawContactId(context, contactData.getId()));

            if (contactData.isHasPhoneNumber()) {
                contactData.setPhoneList(retrievePhoneList(context, id));
            }

            contactDataList.add(contactData);
        }

        cursor.close();

        Log.w("Contacts", String.valueOf(contactDataList));
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
        Log.w("ContactIds in Group[" + groupId + "]", String.valueOf(contactIdList));
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

        Log.w("Email List", String.valueOf(emailList));
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

        Log.w("Phone Model", String.valueOf(phoneList));
        return phoneList;
    }

    public static String getContactGroupId(Context context) {
        String groupId = checkExistGroup(context, AppManHR.GROUP_NAME);

        if (groupId == null) {
            ArrayList<ContentProviderOperation> opsGroup = new ArrayList<>();
            opsGroup.add(ContentProviderOperation.newInsert(ContactsContract.Groups.CONTENT_URI)
                    .withValue(ContactsContract.Groups.TITLE, AppManHR.GROUP_NAME)
                    .withValue(ContactsContract.Groups.GROUP_VISIBLE, true)
                    .withValue(ContactsContract.Groups.GROUP_IS_READ_ONLY, true)
                    .withValue(ContactsContract.Groups.ACCOUNT_NAME, AppManHR.ACCOUNT_NAME)
                    .withValue(ContactsContract.Groups.ACCOUNT_TYPE, AppManHR.ACCOUNT_TYPE)
                    .build());

            try {
                ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, opsGroup);
                groupId = results[0].uri.getLastPathSegment();
                Log.w("create group result %s", groupId);
            } catch (RemoteException | OperationApplicationException e) {
                Log.e("create group failed", AppManHR.GROUP_NAME, e);
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
