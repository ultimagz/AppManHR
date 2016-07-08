package com.appman.intern.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.appman.intern.fragments.ContactsFragment;
import com.appman.intern.fragments.SyncFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ContactsFragment.newInstance(null);
            case 1:
                return SyncFragment.newInstance(null);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "All Contact";
            case 1:
                return "Sync";
        }

        return null;
    }
}
