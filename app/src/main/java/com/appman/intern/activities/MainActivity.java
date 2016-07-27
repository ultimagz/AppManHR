package com.appman.intern.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.appman.intern.AppManHRPreferences;
import com.appman.intern.R;
import com.appman.intern.adapters.SectionsPagerAdapter;
import com.appman.intern.databinding.MainActivityBinding;
import com.appman.intern.fragments.SearchFragment;

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
                            .setCustomAnimations(R.anim.slide_up,R.anim.slide_down,R.anim.slide_up,R.anim.slide_down)
                            //.addSharedElement(sharedElement, transitionName)
                            .replace(R.id.main_content, SearchFragment.newInstance(), "SearchFragment")
                            .addToBackStack("SearchFragment")
                            .commit();
            }
        });

        AppManHRPreferences.setCurrentLanguage(this, "EN");
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
