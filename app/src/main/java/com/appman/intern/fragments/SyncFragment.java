package com.appman.intern.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.appman.intern.AppManHRPreferences;
import com.appman.intern.ContactHelper;
import com.appman.intern.R;
import com.appman.intern.Utils;
import com.appman.intern.databinding.SyncFragmentBinding;
import com.appman.intern.enums.Language;
import com.appman.intern.models.AppContactData;
import com.appman.intern.models.ContactData;
import com.appman.intern.models.LocalContactData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import timber.log.Timber;

public class SyncFragment extends Fragment {

    private SyncFragmentBinding mBinding;

    private final String[] PROJECTION = {
            ContactsContract.Contacts._ID, ContactsContract.Contacts.IN_VISIBLE_GROUP,
            ContactsContract.Contacts.IS_USER_PROFILE, ContactsContract.Contacts.PHOTO_URI, ContactsContract.Contacts.PHOTO_ID,
            ContactsContract.Contacts.PHOTO_FILE_ID, ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
            ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.LOOKUP_KEY, ContactsContract.Contacts.HAS_PHONE_NUMBER
    };

    public static SyncFragment newInstance(Bundle args) {
        SyncFragment fragment = new SyncFragment();
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.sync_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String time = AppManHRPreferences.getLastExportTime(getContext());
        mBinding.syncTime.setText(TextUtils.isEmpty(time) ? "" : time);
        mBinding.exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportToLocalContact();
            }
        });
    }

    private Language getExportLanguage() {
        int btnId = mBinding.langBtnGroup.getCheckedRadioButtonId();
        return btnId == R.id.th_btn ? Language.TH : Language.EN;
    }

    private void exportToLocalContact() {
        Context context = getContext();
        Language lang = getExportLanguage();
        String groupId = ContactHelper.getContactGroupId(getContext());
        List<ContactData> localContactList = ContactHelper.retrieveContacts(getContext(), PROJECTION, groupId);

        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AppContactData> query = realm
                .where(AppContactData.class)
                .beginsWith("firstnameEn", "A", Case.INSENSITIVE);

        RealmResults<AppContactData> results = query.findAll();
        realm.beginTransaction();
        for (AppContactData appContactData : results) {
            LocalContactData dbContactData = appContactData.getLocalContactData();
            boolean exist = false;
            for (ContactData contactData : localContactList) {
                String localId = dbContactData.getLocalId();
                String rawId = contactData.getRawContactId();
                Timber.w("Compare local_id %s | db_id %s", localId, rawId);
                Timber.w("Contact exported %s", String.valueOf(appContactData.isExported()));
                if (/*!appContactData.isExported() && */localId.equalsIgnoreCase(rawId)) {
                    exist = true;
                    dbContactData.setNewValue(appContactData);
                    dbContactData.setLocalId(rawId);
                    appContactData.setLocalContactData(dbContactData);
                    ContactHelper.updateContact(context, appContactData, lang);
                    break;
                }
            }

            if (!exist) {
                String id = ContactHelper.addNewContact(context, appContactData, lang);
                Timber.w("New contact id %s", id);
                dbContactData.setLocalId(id);
            }

            appContactData.setExported(true);
            appContactData.setLocalContactData(dbContactData);
//            realm.insertOrUpdate(appContactData);
        }
        realm.commitTransaction();

        String time = Utils.DATE_FORMAT.format(new Date());
        mBinding.syncTime.setText(String.format("Last export : %s", time));
        AppManHRPreferences.setLastExportTime(getContext(), time);
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
        AppManHRPreferences.setCurrentLanguage(getContext(), btnId == R.id.th_btn ? "TH" : "EN");
    }
}
