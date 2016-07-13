package com.appman.intern.activities;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.appman.intern.AppManHR;
import com.appman.intern.R;
import com.appman.intern.adapters.SectionsPagerAdapter;
import com.appman.intern.databinding.MainActivityBinding;
import com.appman.intern.models.AppContactData;

import java.util.ArrayList;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private MainActivityBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        setSupportActionBar(mBinding.toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        mBinding.viewPager.setAdapter(mSectionsPagerAdapter);
        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                setToolbarTitleByIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
        mBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mBinding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}


            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    public void addNewContact(View view) {
        AppContactData newContact = new AppContactData();
        try {
            ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, newContact.createNewContactProvider(getGroupId()));
            Toast.makeText(this, "Insert contact success", Toast.LENGTH_SHORT).show();

            for (ContentProviderResult result : results) {
                Log.w("insert id %s", String.valueOf(result.uri.getLastPathSegment()));
            }

        } catch (RemoteException | OperationApplicationException e) {
            Log.e("Insert contact failed", String.valueOf(newContact), e);
            Toast.makeText(this, "Insert contact failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void setToolbarTitleByIndex(int index) {
        mBinding.toolbarTitle.setText(mSectionsPagerAdapter.getPageTitle(index));
    }

    private String getGroupId() {
        String groupId = ifGroup(AppManHR.GROUP_NAME);

        if (groupId == null) {
            ArrayList<ContentProviderOperation> opsGroup = new ArrayList<>();
            opsGroup.add(ContentProviderOperation.newInsert(ContactsContract.Groups.CONTENT_URI)
                    .withValue(ContactsContract.Groups.TITLE, AppManHR.GROUP_NAME)
                    .withValue(ContactsContract.Groups.GROUP_VISIBLE, true)
                    .withValue(ContactsContract.Groups.ACCOUNT_NAME, AppManHR.ACCOUNT_NAME)
                    .withValue(ContactsContract.Groups.ACCOUNT_TYPE, AppManHR.ACCOUNT_TYPE)
                    .build());
            try {
                ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, opsGroup);
                for (ContentProviderResult result : results) {
                    Log.w("create group result %s", result.uri.getLastPathSegment());
                }
            } catch (Exception e) {
                Log.e("create group failed", AppManHR.GROUP_NAME, e);
            }

            return ifGroup(AppManHR.GROUP_NAME);
        } else {
            return groupId;
        }
    }

    private String ifGroup(String groupName) {
        String selection = ContactsContract.Groups.DELETED + " = ? AND " + ContactsContract.Groups.GROUP_VISIBLE + " = ? AND " + ContactsContract.Groups.TITLE + " = ?";
        String[] selectionArgs = { "0", "1", groupName };
        Cursor cursor = getContentResolver().query(ContactsContract.Groups.CONTENT_URI, null, selection, selectionArgs, null);

        if (cursor == null)
            return null;

        String id = null, title = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups._ID));
            title = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE));
        }

        cursor.close();

        return id;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.w("Group Id %s", getGroupId());
        setToolbarTitleByIndex(mBinding.viewPager.getCurrentItem());
    }
}
