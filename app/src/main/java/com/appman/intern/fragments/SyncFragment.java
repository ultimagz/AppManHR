package com.appman.intern.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.appman.intern.AppManHRPreferences;
import com.appman.intern.ContactHelper;
import com.appman.intern.R;
import com.appman.intern.Utils;
import com.appman.intern.activities.LoginActivity;
import com.appman.intern.databinding.SyncFragmentBinding;
import com.appman.intern.enums.Language;
import com.appman.intern.models.AppContactData;
import com.appman.intern.models.ContactData;
import com.appman.intern.models.LocalContactData;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
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
        mBinding.lastExportTime.setText(TextUtils.isEmpty(time) ? "" : time);
        mBinding.exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SyncFragmentPermissionsDispatcher.exportToLocalContactWithCheck(SyncFragment.this);
//                exportToLocalContact();
            }
        });

        mBinding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogout();
            }
        });
    }

    private Language getExportLanguage() {
        int btnId = mBinding.langBtnGroup.getCheckedRadioButtonId();
        return btnId == R.id.export_th_btn ? Language.TH : Language.EN;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SyncFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({ Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS })
    void exportToLocalContact() {
        new AsyncTask<Void, Void, Void>() {
            ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Export contacts...");
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getContext();
                Language lang = getExportLanguage();
                String groupId = ContactHelper.getContactGroupId(getContext());
                List<ContactData> localContactList = ContactHelper.retrieveContacts(getContext(), PROJECTION, groupId);

                Realm realm = Realm.getDefaultInstance();
                RealmQuery<AppContactData> query = realm.where(AppContactData.class);
                RealmResults<AppContactData> results = query.findAll();
                realm.beginTransaction();

                for (AppContactData appContactData : results) {
                    LocalContactData dbContactData = appContactData.getLocalContactData();
                    String rawId = getRawContactId(dbContactData, localContactList);

                    if (TextUtils.isEmpty(rawId)) {
                        String id = ContactHelper.addNewContact(context, appContactData, lang);
//                        Timber.w("New contact id %s", id);
                        dbContactData.setLocalId(id);
                    } else {
                        dbContactData.setLocalId(rawId);
                        ContactHelper.updateContact(context, appContactData, lang);
                    }

                    dbContactData.setNewValue(appContactData);
                    appContactData.setExported(true);
                    appContactData.setLocalContactData(dbContactData);
                }
                realm.commitTransaction();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                progressDialog.dismiss();
                String time = Utils.DATE_FORMAT.format(new Date());
                mBinding.lastExportTime.setText(String.format("Last export : %s", time));
                AppManHRPreferences.setLastExportTime(getContext(), time);
            }

        }.execute();
    }

    private String getRawContactId(LocalContactData dbContactData, List<ContactData> localContactList) {
        for (ContactData contactData : localContactList) {
            String rawId = contactData.getRawContactId();
            if (contactData.compareValues(dbContactData)) {
                return rawId;
            }
        }

        return null;
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

    @OnShowRationale({Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS})
    void showRationaleForContact(PermissionRequest request) {
        showRationaleDialog(R.string.permission_contacts_rationale, request);
    }

    @OnNeverAskAgain({ Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS })
    void onContactsNeverAskAgain() {
        Toast.makeText(getContext(), R.string.permission_contacts_never_ask_again, Toast.LENGTH_SHORT).show();
    }

    @OnPermissionDenied({ Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS })
    void onContactsDenied() {
        Toast.makeText(getContext(), R.string.permission_contacts_denied, Toast.LENGTH_SHORT).show();
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(getContext())
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    private void doLogout() {
        AppManHRPreferences.setLogin(getContext(), false);
        Intent backToLogin = new Intent(getActivity(), LoginActivity.class);
        getActivity().startActivity(backToLogin);
        getActivity().finish();
    }
}
