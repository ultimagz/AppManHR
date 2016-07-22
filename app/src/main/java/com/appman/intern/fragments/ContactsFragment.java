package com.appman.intern.fragments;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appman.intern.AppManHRPreferences;
import com.appman.intern.DatabaseHelper;
import com.appman.intern.R;
import com.appman.intern.Utils;
import com.appman.intern.adapters.ContactListAdapter;
import com.appman.intern.databinding.ContactFragmentBinding;
import com.appman.intern.enums.Language;
import com.appman.intern.models.AppContactData;
import com.appman.intern.models.LocalContactData;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
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

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import timber.log.Timber;

public class ContactsFragment extends Fragment implements View.OnClickListener, Callback {

    ContactFragmentBinding mBinding;
    ContactListAdapter mAdapter;
    DatabaseHelper mHelper;
    ProgressDialog mProgressDialog;

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
        mProgressDialog = new ProgressDialog(getActivity());
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayIndex();
        mBinding.swipeContainer.setColorSchemeResources(R.color.red,R.color.blue,R.color.green);
        mBinding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getContactListFromServer();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initContactList();
//        getContactsListFromFile();
    }

    private void initContactList() {
        if (Utils.isNetworkConnected(getContext())) {
            getContactListFromServer();
        } else {
            getContactListFromDatabase();
        }
    }

    private void getContactListFromDatabase() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AppContactData> query = realm.where(AppContactData.class);
        RealmResults<AppContactData> results = query.findAllSorted("firstnameEn", Sort.ASCENDING);
        mAdapter = new ContactListAdapter(getActivity(), results);
        mBinding.contactList.setAdapter(mAdapter);
    }

    private void getContactsListFromFile() {
        try {
            InputStream json = getActivity().getAssets().open("sample_contact.json");
            String jsonString = IOUtils.toString(json, "UTF-8");
            updateAdapter(jsonString);
        } catch (IOException e) {
            updateAdapter("[]");
        }
    }

    private void displayIndex() {
        TextView textView;
        String[] alphabets = getResources().getStringArray(R.array.alphabet);
        for (String alphabet : alphabets) {
            textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.side_index_item, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
            textView.setLayoutParams(params);
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
        Type jsonType = new TypeToken<ArrayList<AppContactData>>() {}.getType();
        List<AppContactData> contactList = new Gson().fromJson(jsonString, jsonType);
        Collections.sort(contactList, AppContactData.getComparator(Language.EN));
        mAdapter = new ContactListAdapter(getActivity(), contactList);
        mBinding.contactList.setAdapter(mAdapter);
    }

    private void getContactListFromServer() {
        mBinding.swipeContainer.setRefreshing(false);
        mProgressDialog.setMessage("Sync contacts");
        mProgressDialog.show();

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(Utils.URL).build();

        okHttpClient.networkInterceptors().add(new StethoInterceptor());
        okHttpClient
                .newCall(request)
                .enqueue(this);
    }

    private void saveDatabase(String jsonString) {
        Type jsonType = new TypeToken<ArrayList<AppContactData>>() {}.getType();
        List<AppContactData> contactList = new Gson().fromJson(jsonString, jsonType);
        Collections.sort(contactList, AppContactData.getComparator(Language.EN));

        AppContactData dbContactData;
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (AppContactData contactData : contactList) {

            RealmQuery<AppContactData> query = realm.where(AppContactData.class);
            query.equalTo("id", contactData.getId());
            dbContactData = query.findFirst();

            if (dbContactData == null) {
                contactData.setLocalContactData(new LocalContactData(contactData));
                realm.insertOrUpdate(contactData);
            } else if (!dbContactData.getUpdateTime().equals(contactData.getUpdateTime())) {
                contactData.setLocalContactData(dbContactData.getLocalContactData());
                contactData.setExported(false);
                realm.insertOrUpdate(contactData);
            }
        }

        realm.commitTransaction();
    }

    @Override
    public void onFailure(final Request request, IOException e) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
                showRequestFailDialog(request.toString());
            }
        });
    }

    @Override
    public void onResponse(final Response response) throws IOException {
        final String result = response.body().string();
        final boolean success = response.isSuccessful();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
                if (success) {
                    updateAdapter(result);
                    saveDatabase(result);
                } else {
                    showRequestFailDialog(response.message());
                }
            }
        });
    }

    private void showRequestFailDialog(String message) {
        Timber.e("Request fail. %s", message);
        //TODO
    }
}

