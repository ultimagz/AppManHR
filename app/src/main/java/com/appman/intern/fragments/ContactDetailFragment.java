package com.appman.intern.fragments;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appman.intern.R;
import com.appman.intern.adapters.ContactDetailAdapter;
import com.appman.intern.databinding.ContactDetailFragmentBinding;
import com.appman.intern.enums.ContactDetailType;
import com.appman.intern.enums.Language;
import com.appman.intern.interfaces.ContactDetailClickHandler;
import com.appman.intern.models.ContactDetailRowModel;
import com.appman.intern.models.SearchableContactData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import timber.log.Timber;

@RuntimePermissions
public class ContactDetailFragment extends Fragment implements ContactDetailClickHandler {

    private SearchableContactData mContactData;
    private Language mLanguage;
    ContactDetailFragmentBinding mBinding;
    ContactDetailAdapter mAdapter;
    List<ContactDetailRowModel> mList = new ArrayList<>();
    String mTransitionName = null;

    public static ContactDetailFragment newInstance(SearchableContactData contactData, Language lang, String transitionName) {
        ContactDetailFragment fragment = new ContactDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("contactData", Parcels.wrap(contactData));
        args.putString("lang", lang.name());
        args.putString("transitionName", transitionName);
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
            mTransitionName = args.getString("transitionName", null);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.contact_detail_fragment, container, false);
        ViewCompat.setTransitionName(mBinding.contactDetailImg, mTransitionName);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.setViewLanguage(mLanguage);
        mBinding.setContactDetailData(mContactData);

        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mAdapter = new ContactDetailAdapter(mList, this);
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        updateDetail();
        Glide.with(getContext())
                .load(TextUtils.isEmpty(mContactData.getImage()) ? null : Base64.decode(mContactData.getImage(), Base64.DEFAULT))
                .error(R.drawable.dummy_photo)
                .placeholder(R.drawable.dummy_photo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate().dontTransform()
                .into(mBinding.contactDetailImg);
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
        }
    }

    @NeedsPermission(Manifest.permission.CALL_PHONE)
    void callPhone(String phoneNumber) {
        Timber.w("Call %s", phoneNumber);
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        phoneIntent.setData(Uri.parse("tel:" + "083314860"));
        startActivity(phoneIntent);
    }

    @NeedsPermission(Manifest.permission.SEND_SMS)
    void sendSMS(String phoneNumber) {
        Timber.w("Msg %s", phoneNumber);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, "message", null, null);

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", "");
        sendIntent.setType("vnd.android-dir/mms-sms");
        sendIntent.setData(Uri.parse("sms:" + "083314860"));
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
