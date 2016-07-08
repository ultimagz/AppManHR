package com.appman.intern.fragments;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.appman.intern.ContactListAdapter;
import com.appman.intern.R;
import com.appman.intern.models.ContactData;
import com.appman.intern.models.EmailData;
import com.appman.intern.models.PhoneData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ContactsFragment extends Fragment implements View.OnClickListener {

    private final String[] PROJECTION = {
            ContactsContract.Contacts._ID, ContactsContract.Contacts.IN_VISIBLE_GROUP,
            ContactsContract.Contacts.IS_USER_PROFILE, ContactsContract.Contacts.PHOTO_URI, ContactsContract.Contacts.PHOTO_ID,
            ContactsContract.Contacts.PHOTO_FILE_ID, ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
            ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.LOOKUP_KEY, ContactsContract.Contacts.HAS_PHONE_NUMBER
    };

    String[] list = { "Aerith Gainsborough", "Barret Wallace", "Cait Sith"
            , "Cid Highwind", "Cloud Strife", "RedXIII", "Sephiroth"
            , "Tifa Lockhart", "Vincent Valentine", "Yuffie Kisaragi"
            , "ZackFair"};

    Map<String, Integer> mapIndex;
    ListView listView;
    LinearLayout indexLayout;
    ContactListAdapter adapter;

    public static ContactsFragment newInstance(Bundle args) {
        ContactsFragment fragment = new ContactsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        indexLayout = (LinearLayout) rootView.findViewById(R.id.side_index);
        listView = (ListView) rootView.findViewById(R.id.listView);
        getIndexList();
        displayIndex();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new ContactListAdapter(getActivity(), createContactsList());
        listView.setAdapter(adapter);
    }

    private List<ContactData> createContactsList() {
        String prev = "";
        List<ContactData> contactList = retrieveContacts(getContext());
        List<ContactData> all = new ArrayList<>();
        for (ContactData contactData : contactList) {
            String alpha = contactData.getDisplayName().substring(0, 1).toUpperCase();
            if (!alpha.equalsIgnoreCase(prev)) {
                prev = alpha;
                all.add(new ContactData(alpha, true));
            }

            all.add(contactData);
        }

        Log.w("All contacts", all.toString());
        return all;
    }

    private void getIndexList() {
        mapIndex = new HashMap<>();

        String[] alphabets = getResources().getStringArray(R.array.alphabet);
        int index = 0;
        for (String alphabet : alphabets) {
            mapIndex.put(alphabet, index);
            index++;
        }
    }

    private void displayIndex() {
        TextView textView;
        List<String> indexList = new ArrayList<>(mapIndex.keySet());
        Collections.sort(indexList);

        for (String index : indexList) {
            textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.side_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(this);
            indexLayout.addView(textView);
        }
    }

    @Override
    public void onClick(View view) {
        TextView selectedIndex = (TextView) view;
        listView.setSelection(mapIndex.get(selectedIndex.getText()));
    }

    public List<ContactData> retrieveContacts(Context context) { //This Context parameter is nothing but your Activity class's Context
        List<ContactData> contactDataList = new ArrayList<>();

        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1' ";
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, selection, null, null);
        if (cursor == null) {
            return contactDataList;
        }

//        int contactsCount = cursor.getCount(); // get how many contacts you have in your contacts list
        Timber.w("column(s)\n%s", Arrays.toString(cursor.getColumnNames()));
        while(cursor.moveToNext()) {
            ContactData contactData = new ContactData(false);

            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String thumbnailUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
            String photoId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
            String photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
            String photoFileId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_FILE_ID));
            String isUserProfile = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.IS_USER_PROFILE));
            String inVisibleGroup = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.IN_VISIBLE_GROUP));
            String hasPhoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

            contactData.setId(id);
            contactData.setValue(displayName);
            contactData.setDisplayName(displayName);
            contactData.setThumbnailUri(thumbnailUri);
            contactData.setPhotoUri(photoUri);
            contactData.setPhotoId(photoId);
            contactData.setPhotoFileId(photoFileId);
            contactData.setInVisibleGroup(TextUtils.equals(inVisibleGroup, "1"));
            contactData.setIsUserProfile(TextUtils.equals(isUserProfile, "1"));
            contactData.setHasPhoneNumber(TextUtils.equals(hasPhoneNumber, "1"));
            contactData.setLookupKey(lookupKey);
            contactData.setEmailList(retrieveEmailData(context, id));
            contactData.setRawContactId(retrieveRawContactIds(context, id));

            if (TextUtils.equals(hasPhoneNumber, "1")) {
                contactData.setPhoneList(retrievePhoneData(context, id));
            }

            contactDataList.add(contactData);
        }

        cursor.close();

        Log.w("Contacts", String.valueOf(contactDataList));
        return contactDataList;
    }

    private String retrieveRawContactIds(Context context, String contactId) {
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

    private List<EmailData> retrieveEmailData(Context context, String contactId) {
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

    private List<PhoneData> retrievePhoneData(Context context, String contactId) {
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

