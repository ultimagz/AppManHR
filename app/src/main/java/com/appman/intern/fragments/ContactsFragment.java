package com.appman.intern.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appman.intern.AppManHRPreferences;
import com.appman.intern.DatabaseHelper;
import com.appman.intern.R;
import com.appman.intern.adapters.ContactListAdapter;
import com.appman.intern.databinding.ContactFragmentBinding;
import com.appman.intern.enums.Language;
import com.appman.intern.models.AppContactData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactsFragment extends Fragment implements View.OnClickListener {

    private final String[] PROJECTION = {
            ContactsContract.Contacts._ID, ContactsContract.Contacts.IN_VISIBLE_GROUP,
            ContactsContract.Contacts.IS_USER_PROFILE, ContactsContract.Contacts.PHOTO_URI, ContactsContract.Contacts.PHOTO_ID,
            ContactsContract.Contacts.PHOTO_FILE_ID, ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
            ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.LOOKUP_KEY, ContactsContract.Contacts.HAS_PHONE_NUMBER
    };

    ContactFragmentBinding mBinding;
    ContactListAdapter mAdapter;
    public static String URL = "http://hr.appmanproject.com/api/user/list";

    DatabaseHelper mHelper;
    SQLiteDatabase mDb;


    public static ContactsFragment newInstance(Bundle args) {
        ContactsFragment fragment = new ContactsFragment();
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.contact_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayIndex();
    }

    @Override
    public void onResume() {
        super.onResume();

        getContactListFromServer();
        //getContactListFromDatabase();
        //getContactsListFromFile();
    }

    private void getContactsListFromFile() {
        List<AppContactData> contactList = new ArrayList<>();
        try {
            InputStream json = getActivity().getAssets().open("sample_contact.json");
            AppContactData data = new AppContactData();
            Gson gson = new GsonBuilder().serializeNulls().create();
            String jsonString = gson.toJson(data);
            updateAdapter(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayIndex() {
        TextView textView;
        String[] alphabets = getResources().getStringArray(R.array.alphabet);
        for (String alphabet : alphabets) {
            textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.side_index_item, null);
            textView.setText(alphabet);
            textView.setOnClickListener(this);
            mBinding.sideIndex.addView(textView);
        }
    }


    @Override
    public void onClick(View view) {
        TextView selectedIndex = (TextView) view;
        String alphabet = selectedIndex.getText().toString();
        int index = mAdapter.getMapIndex(alphabet);
        if (index != -1)
            mBinding.contactList.setSelection(index);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_tab, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Language lang = AppManHRPreferences.getCurrentLanguage(getContext());
//        MenuItem menuItem = menu.findItem(R.id.lang_switch);
//        RelativeLayout relativeLayout = (RelativeLayout) menuItem.getActionView();
//        Switch switchBtn = (Switch) relativeLayout.findViewById(R.id.switch_lang_btn);
//        switchBtn.setChecked(lang == Language.TH);
//        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                toggleLanguage(isChecked);
//            }
//        });
    }

    private void toggleLanguage(boolean isChecked) {
        AppManHRPreferences.setCurrentLanguage(getContext(), isChecked ? "TH" : "EN");
    }

    private void updateAdapter(String jsonString) {
        Type jsonType = new TypeToken<ArrayList<AppContactData>>() {
        }.getType();
        List<AppContactData> contactList = new Gson().fromJson(jsonString, jsonType);
        Collections.sort(contactList, AppContactData.getComparator(Language.EN));
        mAdapter = new ContactListAdapter(getActivity(), contactList);
        mBinding.contactList.setAdapter(mAdapter);
    }

    public void getContactListFromServer() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                OkHttpClient okHttpClient = new OkHttpClient();

                Request.Builder builder = new Request.Builder();

                Request request = builder.url(URL).build();


                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        return response.body().string();
                    } else {
                        return "Not Success - code : " + response.code();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Error - " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {

                updateAdapter(result);

                saveDatabase(result);
                Log.e("re", result);
            }
        }.execute();
    }


    public void saveDatabase(String jsonString) {
        Type jsonType = new TypeToken<ArrayList<AppContactData>>() {
        }.getType();
        List<AppContactData> contactList = new Gson().fromJson(jsonString, jsonType);
        Collections.sort(contactList, AppContactData.getComparator(Language.EN));

        mHelper = new DatabaseHelper(getActivity());

        mDb = mHelper.getWritableDatabase();

        Cursor mCursor = mDb.rawQuery("SELECT * FROM " + DatabaseHelper.DBTABLE, null);

        if (mCursor.getCount() == 0) {
            insertData(contactList);
            Log.e("suss", "full");


        } else {
            deleteData();
            insertData(contactList);

            Log.e("suss", "yes");
        }


    }

    public void getContactListFromDatabase() {

        mHelper = new DatabaseHelper(getActivity());

        mDb = mHelper.getReadableDatabase();
        AppContactData contact ;
        List<AppContactData> contactList = new ArrayList<>();


        Cursor mCursor = mDb.query(DatabaseHelper.DBTABLE, new String[]{"contact_id", "fistName_TH", "lastName_TH",
                "nickName_TH", "fistName_EN", "lastName_EN", "nickName_EN", "position", "email",
                "mobile", "workphone", "line"}, null, null, null, null, "fistName_EN ASC");


        mCursor.moveToFirst();


        while (!mCursor.isAfterLast()) {
            //contact = new ContactData(mCursor.getString(0), mCursor.getString(1), mCursor.getString(2), mCursor.getString(3), mCursor.getString(4), mCursor.getString(5), mCursor.getString(mCursor.getColumnIndex()), mCursor.getString(7), mCursor.getString(8), mCursor.getString(9), mCursor.getString(10), mCursor.getString(11));
            contact = new AppContactData();
            contact.setId(mCursor.getString(mCursor.getColumnIndexOrThrow(DatabaseHelper.contactID)));
            contact.setFirstnameTh(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.fistNameTH)));
            contact.setLastnameTh(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.lastNameTH)));
            contact.setNicknameTh(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.nickNameTH)));
            contact.setFirstnameEn(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.fistNameEN)));
            contact.setLastnameEn(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.lastNameEN)));
            contact.setNicknameEn(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.nickNameEN)));
            contact.setPosition(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.position)));
            contact.setEmail(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.email)));
            contact.setMobile(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.mobile)));
            contact.setWorkPhone(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.workphone)));
            contact.setLine(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.line)));


            contactList.add(contact);
            mCursor.moveToNext();


        }

        mCursor.close();
        Log.e("contactList", contactList.toString());

        updateAdapter(contactList.toString());
    }

    public void insertData(List<AppContactData> contactList) {
        for (AppContactData a : contactList) {

            mDb.execSQL("INSERT INTO " + DatabaseHelper.DBTABLE + " ("
                    + DatabaseHelper.contactID + ", " + DatabaseHelper.fistNameTH
                    + ", " + DatabaseHelper.lastNameTH
                    + ", " + DatabaseHelper.nickNameTH
                    + ", " + DatabaseHelper.fistNameEN
                    + ", " + DatabaseHelper.lastNameEN
                    + ", " + DatabaseHelper.nickNameEN
                    + ", " + DatabaseHelper.position
                    + ", " + DatabaseHelper.email
                    + ", " + DatabaseHelper.mobile
                    + ", " + DatabaseHelper.workphone
                    + ", " + DatabaseHelper.line
                    + ", " + DatabaseHelper.updateTime
                    + ") VALUES ('" + a.getId()
                    + "', '" + a.getFirstnameTh()
                    + "', '" + a.getLastnameTh()
                    + "', '" + a.getNicknameTh()
                    + "', '" + a.getFirstnameEn()
                    + "', '" + a.getLastnameEn()
                    + "', '" + a.getNicknameEn()
                    + "', '" + a.getPosition()
                    + "', '" + a.getEmail()
                    + "', '" + a.getMobile()
                    + "', '" + a.getWorkPhone()
                    + "', '" + a.getLine()
                    + "', '" + a.getUpdate()
                    + "');");
        }
    }

    public void deleteData(){
        mDb.execSQL("DELETE FROM "+DatabaseHelper.DBTABLE+";");
        Log.e("delete","yes");

    }

}

