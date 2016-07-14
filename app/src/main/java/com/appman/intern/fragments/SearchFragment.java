package com.appman.intern.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appman.intern.AppManHR;
import com.appman.intern.R;
import com.appman.intern.adapters.ContactListAdapter;
import com.appman.intern.databinding.ContactFragmentBinding;
import com.appman.intern.databinding.SearchFragmentBinding;
import com.appman.intern.enums.Language;
import com.appman.intern.models.AppContactData;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements View.OnClickListener {

    private SearchFragmentBinding mBinding;

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        //fragment.setArguments(args);
        //fragment.setHasOptionsMenu(true);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.search_fragment, container, false);
        return mBinding.getRoot();
    }

    ContactListAdapter mAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayIndex();

        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter = new ContactListAdapter(getActivity(), getContactsListFromFile());
        mBinding.contactList.setAdapter(mAdapter);
    }

    private List<AppContactData> getContactsListFromFile() {
        List<AppContactData> contactList = new ArrayList<>();
        try {
            InputStream json = getActivity().getAssets().open("sample_contact.json");
            String jsonString = IOUtils.toString(json, "UTF-8");
            Type jsonType = new TypeToken<ArrayList<AppContactData>>(){}.getType();
            contactList = AppManHR.GSON.fromJson(jsonString, jsonType); //ContactHelper.retrieveContacts(getContext(, PROJECTION);
            Collections.sort(contactList, AppContactData.getComparator(Language.EN));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contactList;
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

}
