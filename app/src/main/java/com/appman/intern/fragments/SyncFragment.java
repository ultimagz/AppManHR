package com.appman.intern.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.appman.intern.R;

public class SyncFragment extends Fragment {

    Switch sw;
    Toolbar toolbar;
    Button th;

    public static SyncFragment newInstance(Bundle args) {
        SyncFragment fragment = new SyncFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sync, container, false);
        th = (Button)rootView.findViewById(R.id.Th_btn);

        th.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return inflater.inflate(R.layout.fragment_sync, container, false);
    }

}
