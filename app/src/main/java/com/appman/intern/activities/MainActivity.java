package com.appman.intern.activities;

import android.content.ContentProviderResult;
import android.content.OperationApplicationException;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.appman.intern.AppManHRPreferences;
import com.appman.intern.ContactHelper;
import com.appman.intern.R;
import com.appman.intern.adapters.SectionsPagerAdapter;
import com.appman.intern.databinding.MainActivityBinding;
import com.appman.intern.enums.Language;
import com.appman.intern.fragments.SearchFragment;
import com.appman.intern.models.AppContactData;

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

        mBinding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager
                            .beginTransaction()
                            .add(R.id.main_content, SearchFragment.newInstance(), "SearchFragment")
                            .addToBackStack("SearchFragment")
                            .commit();

            }
        });

        AppManHRPreferences.setCurrentLanguage(this, "EN");
    }

    public void addNewContact(View view) {
        AppContactData newContact = new AppContactData();
        try {
            String groupId = ContactHelper.getContactGroupId(this);
            Language lang = AppManHRPreferences.getCurrentLanguage(this);
            ContentProviderResult[] results =
                    getContentResolver().applyBatch(
                            ContactsContract.AUTHORITY,
                            newContact.createNewContactProvider(lang, groupId));

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

    @Override
    protected void onResume() {
        super.onResume();
        setToolbarTitleByIndex(mBinding.viewPager.getCurrentItem());
    }
}
