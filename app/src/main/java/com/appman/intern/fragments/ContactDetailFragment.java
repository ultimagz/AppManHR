package com.appman.intern.fragments;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appman.intern.R;
import com.appman.intern.adapters.ContactAdapter;
import com.appman.intern.databinding.ContactDetailFragmentBinding;
import com.appman.intern.enums.ContactDetailType;
import com.appman.intern.enums.Language;
import com.appman.intern.interfaces.ContactDetailClickHandler;
import com.appman.intern.models.AppContactData;
import com.appman.intern.models.ContactDetailRowModel;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import timber.log.Timber;

@RuntimePermissions
public class ContactDetailFragment extends Fragment implements ContactDetailClickHandler {

    private AppContactData mContactData;
    private Language mLanguage;
    ContactDetailFragmentBinding mBinding;
    ContactAdapter mAdapter;
    List<ContactDetailRowModel> mList = new ArrayList<>();

    public static ContactDetailFragment newInstance(AppContactData contactData, Language lang) {
        ContactDetailFragment fragment = new ContactDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("contactData", Parcels.wrap(contactData));
        args.putString("lang", lang.name());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mContactData = Parcels.unwrap(args.getParcelable("contactData"));
            mLanguage = Language.valueOf(args.getString("lang", Language.EN.name()));
        }
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
        mBinding.setViewLanguage(mLanguage);
        mBinding.setContactDetailData(mContactData);

        mBinding.contactJob.setText(mContactData.getPosition());
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mAdapter = new ContactAdapter(mList, this);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View view, ContactDetailRowModel dataModel) {
        switch (view.getId()) {
            case R.id.contact_phone_msg:
                ContactDetailFragmentPermissionsDispatcher.sendSMSWithCheck(this, dataModel.getData());
                break;
            case R.id.contact_phone_call:
                ContactDetailFragmentPermissionsDispatcher.callPhoneWithCheck(this, dataModel.getData());
                break;
            case R.id.contact_email_btn:
                sendEMail(dataModel.getData());
                break;
            case R.id.contact_line_btn:
                Timber.w("LINE %s", dataModel.getData());
                break;
        }
    }

    @NeedsPermission(Manifest.permission.CALL_PHONE)
    void callPhone(String phoneNumber) {
        Timber.w("Call %s", phoneNumber);
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        phoneIntent.setData(Uri.parse("tel:" + "0830639432"));
        startActivity(phoneIntent);
    }

    @NeedsPermission(Manifest.permission.SEND_SMS)
    void sendSMS(String phoneNumber) {
        Timber.w("Msg %s", phoneNumber);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, "message", null, null);

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", "message");
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivity(sendIntent);
    }

    private void sendEMail(String email) {
        Timber.w("E-Mail %s", email);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + email));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Message");
        startActivity(Intent.createChooser(emailIntent, "Send E-Mail..."));
    }
}
