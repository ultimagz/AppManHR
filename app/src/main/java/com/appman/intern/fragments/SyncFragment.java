package com.appman.intern.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.appman.intern.AppManHRPreferences;
import com.appman.intern.R;
import com.appman.intern.databinding.SyncFragmentBinding;
import com.appman.intern.enums.Language;

public class SyncFragment extends Fragment {

    private SyncFragmentBinding mBinding;

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
        mBinding.thBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });

        mBinding.enBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });
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
