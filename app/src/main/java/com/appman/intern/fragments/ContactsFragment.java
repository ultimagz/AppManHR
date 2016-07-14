package com.appman.intern.fragments;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.appman.intern.AppManHR;
import com.appman.intern.AppManHRPreferences;
import com.appman.intern.R;
import com.appman.intern.adapters.ContactListAdapter;
import com.appman.intern.databinding.ContactFragmentBinding;
import com.appman.intern.enums.Language;
import com.appman.intern.models.AppContactData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Response;

import org.apache.commons.io.IOUtils;

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
    String result;
    String resultText;
    List<AppContactData> list;

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
//        getContactsListFromFile();
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
        Type jsonType = new TypeToken<ArrayList<AppContactData>>() {}.getType();
        List<AppContactData> contactList = new Gson().fromJson(jsonString, jsonType);
        Collections.sort(contactList, AppContactData.getComparator(Language.EN));
        mAdapter = new ContactListAdapter(getActivity(), contactList);
        mBinding.contactList.setAdapter(mAdapter);
    }

    public void getContactListFromServer() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                com.squareup.okhttp.OkHttpClient okHttpClient = new com.squareup.okhttp.OkHttpClient();

                com.squareup.okhttp.Request.Builder builder = new com.squareup.okhttp.Request.Builder();
                com.squareup.okhttp.Request request = builder.url("http://hr.appmanproject.com/api/user/list").build();

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
            protected void onPostExecute(String output) {
                updateAdapter(output);
            }

        }.execute();
    }
}

