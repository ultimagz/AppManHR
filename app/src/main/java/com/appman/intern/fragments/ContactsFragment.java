package com.appman.intern.fragments;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appman.intern.AppManHR;
import com.appman.intern.AppManHRPreferences;
import com.appman.intern.ContactHelper;
import com.appman.intern.DatabaseHelper;
import com.appman.intern.R;
import com.appman.intern.adapters.ContactListAdapter;
import com.appman.intern.databinding.ContactFragmentBinding;
import com.appman.intern.enums.Language;
import com.appman.intern.models.AppContactData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
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
    DatabaseHelper mHelper;

    public static ContactsFragment newInstance(Bundle args) {
        ContactsFragment fragment = new ContactsFragment();
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.contact_fragment, container, false);
        mHelper = new DatabaseHelper(getActivity());
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getContactListFromServer();
                Log.e("F5","suss");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        getContactListFromServer();
//        getContactListFromDatabase();
//        getContactsListFromFile();
        String groupId = ContactHelper.getContactGroupId(getContext());
        ContactHelper.retrieveContacts(getContext(), PROJECTION, groupId);
        mAdapter = new ContactListAdapter(getActivity(), getContactsListFromFile());
        mBinding.contactList.setAdapter(mAdapter);
    }

    private List<AppContactData> getContactsListFromFile() {
        List<AppContactData> contactList = new ArrayList<>();
        try {
            InputStream json = getActivity().getAssets().open("sample_contact.json");
            String jsonString = IOUtils.toString(json, "UTF-8");
            updateAdapter(jsonString);
            return contactList;
        } catch (IOException e) {
            return contactList;
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
        MenuItem menuItem = menu.findItem(R.id.lang_switch);
        RelativeLayout relativeLayout = (RelativeLayout) menuItem.getActionView();
        RadioGroup langGroup = (RadioGroup) relativeLayout.findViewById(R.id.lang_btn_group);
        langGroup.check(lang == Language.TH ? R.id.th_btn : R.id.en_btn);
        langGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                toggleLanguage(id);
            }
        });
    }

    private void toggleLanguage(int btnId) {
        boolean isThai = btnId == R.id.th_btn;
        AppManHRPreferences.setCurrentLanguage(getContext(), isThai ? "TH" : "EN");
        mAdapter.setLanguage(isThai ? Language.TH : Language.EN);
        mAdapter.notifyDataSetChanged();
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

                Request request = builder.url(AppManHR.URL).build();


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
                mBinding.swipeContainer.setRefreshing(false);
                Log.e("re", result);
            }
        }.execute();
    }


    public void saveDatabase(String jsonString) {
        Type jsonType = new TypeToken<ArrayList<AppContactData>>() {}.getType();
        List<AppContactData> contactList = new Gson().fromJson(jsonString, jsonType);
        Collections.sort(contactList, AppContactData.getComparator(Language.EN));

        AppContactData dbContactData;
        for (AppContactData contactData : contactList) {
            dbContactData = mHelper.getContactById(contactData.getId());
            if (dbContactData == null) {
                mHelper.insertContactData(contactData);
            } else if (!dbContactData.getUpdateTime().equals(contactData.getUpdateTime())) {
                mHelper.updateContactData(contactData);
            }
        }
    }
}

