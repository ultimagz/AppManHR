package com.appman.intern.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appman.intern.R;
import com.appman.intern.adapters.ContactAdapter;
import com.appman.intern.adapters.ContactListAdapter;
import com.appman.intern.databinding.ContactDetailFragmentBinding;
import com.appman.intern.enums.Language;
import com.appman.intern.models.AppContactData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.parceler.Parcels;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactDetailFragment extends Fragment {

    private AppContactData mContactData;
    ContactDetailFragmentBinding mBinding;
    ContactAdapter mAdapter;
    List<AppContactData> mList = new ArrayList<>();

    public static ContactDetailFragment newInstance(AppContactData contactData) {
        ContactDetailFragment fragment = new ContactDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("contactData", Parcels.wrap(contactData));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContactData = Parcels.unwrap(getArguments().getParcelable("contactData"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.contact_detail_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.setContactData(mContactData);
        update(mContactData);
        mBinding.contactJob.setText(mContactData.getPosition());
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void update(AppContactData mContactData) {
        mList.add(new AppContactData(mContactData.getMobile(),AppContactData.MOBILE_TYPE));
        mList.add(new AppContactData(mContactData.getWorkPhone(),AppContactData.WORK_PHONE_TYPE));
        mList.add(new AppContactData(mContactData.getEmail(),AppContactData.E_MAIL_TYPE));
        mList.add(new AppContactData(mContactData.getLineID(),AppContactData.ID_LINE_TYPE));
        mAdapter = new ContactAdapter(mList);
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
