package com.appman.intern;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.appman.intern.models.ContactData;
import com.appman.intern.models.EmailData;
import com.appman.intern.models.PhoneData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class ContactHelper {

    public static List<ContactData> retrieveContacts(Context context, String[] projection) { //This Context parameter is nothing but your Activity class's Context
        List<ContactData> contactDataList = new ArrayList<>();

        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1' ";
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, selection, null, null);
        if (cursor == null) {
            return contactDataList;
        }

//        int contactsCount = cursor.getCount(); // get how many contacts you have in your contacts list
        Timber.w("column(s)\n%s", Arrays.toString(cursor.getColumnNames()));
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
            EmailData emailModel = new EmailData();
            int emailType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email._ID));
            String refContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID));
            String rawContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID));
            String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.LOOKUP_KEY));
            String emailAddress = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));

            emailModel.setId(id);
            emailModel.setContactId(refContactId);
            emailModel.setRawContactId(rawContactId);
            emailModel.setLookupKey(lookupKey);
            emailModel.setEmailType(emailType);
            emailModel.setEmailAddress(emailAddress);

            emailList.add(emailModel);
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
            PhoneData phoneModel = new PhoneData();
            int phoneType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
            String refContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            String rawContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
            String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY));
            String phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phoneModel.setId(id);
            phoneModel.setContactId(refContactId);
            phoneModel.setRawContactId(rawContactId);
            phoneModel.setLookupKey(lookupKey);
            phoneModel.setPhoneNo(phoneNo);
            phoneModel.setPhoneType(phoneType);

            phoneList.add(phoneModel);
        }

        cursor.close();

        Log.w("Phone Model", String.valueOf(phoneList));
        return phoneList;
    }
}
