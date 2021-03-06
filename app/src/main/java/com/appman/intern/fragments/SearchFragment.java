package com.appman.intern.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appman.intern.ContactHelper;
import com.appman.intern.R;
import com.appman.intern.Utils;
import com.appman.intern.adapters.SearchContactListAdapter;
import com.appman.intern.databinding.SearchFragmentBinding;

public class SearchFragment extends Fragment {

    private SearchFragmentBinding mBinding;
    SearchContactListAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        //fragment.setArguments(args);
        //fragment.setHasOptionsMenu(true);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.search_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mBinding.searchClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.searchText.setText("");
            }
        });

        mBinding.searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s);
                mBinding.searchClearBtn.setVisibility(s.length() == 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mAdapter = new SearchContactListAdapter(getActivity(), ContactHelper.getSearchableContactListFromDatabase(getContext()));
        mLayoutManager = new LinearLayoutManager(getContext());
        mBinding.contactList.setLayoutManager(mLayoutManager);
        mBinding.contactList.setAdapter(mAdapter);
        mBinding.contactList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Utils.hideSoftKeyboard(getActivity());
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Utils.hideSoftKeyboard(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_tab, menu);
    }
}
