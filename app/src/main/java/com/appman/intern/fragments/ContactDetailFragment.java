package com.appman.intern.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appman.intern.R;
import com.appman.intern.adapters.ContactAdapter;
import com.appman.intern.databinding.ContactDetailFragmentBinding;
import com.appman.intern.enums.ContactDetailType;
import com.appman.intern.models.AppContactData;
import com.appman.intern.models.ContactDetailRowModel;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ContactDetailFragment extends Fragment {

    private AppContactData mContactData;
    ContactDetailFragmentBinding mBinding;
    ContactAdapter mAdapter;
    List<ContactDetailRowModel> mList = new ArrayList<>();

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

        mBinding.contactJob.setText(mContactData.getPosition());
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mAdapter = new ContactAdapter(mList);
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateDetail();
    }

    private void updateDetail() {
        mList.add(new ContactDetailRowModel(mContactData.getMobile(), ContactDetailType.MOBILE));
        mList.add(new ContactDetailRowModel(mContactData.getWorkPhone(), ContactDetailType.WORK_PHONE));
        mList.add(new ContactDetailRowModel(mContactData.getEmail(), ContactDetailType.E_MAIL));
        mList.add(new ContactDetailRowModel(mContactData.getLineID(), ContactDetailType.LINE));
        mAdapter.setList(mList);
    }
}
