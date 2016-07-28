package com.appman.intern.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.appman.intern.R;
import com.appman.intern.databinding.SearchContactDataRowBinding;
import com.appman.intern.databinding.SearchContactHeaderRowBinding;
import com.appman.intern.enums.Language;
import com.appman.intern.fragments.ContactDetailFragment;
import com.appman.intern.models.SearchableContactData;
import com.appman.intern.viewholders.SearchContactRowViewHolder;
import com.appman.intern.viewholders.SearchContactSectionViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    public static final List<String> SIDE_INDEX_EN = new ArrayList<>(Arrays.asList(new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"}));

    FragmentActivity mActivity;
    List<SearchableContactData> mOriginalList;
    List<SearchableContactData> mFilterList;
    LayoutInflater mInflater;
    Map<String, Integer> mapIndex = new HashMap<>();
    ItemFilter mFilter = new ItemFilter();
    Language mLanguage = Language.EN;

    public SearchContactListAdapter(FragmentActivity activity, List<SearchableContactData> contactList) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        setList(contactList);
    }

    public void setList(List<SearchableContactData> contactList) {
        mOriginalList = new ArrayList<>(contactList);
        mFilterList = createSectionList(contactList);
        createIndexList(mFilterList);
        notifyDataSetChanged();
    }

    public SearchableContactData getItem(int position) {
        if (position >= 0 && position < mFilterList.size())
            return mFilterList.get(position);
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        switch (viewType) {
            case 0:
                SearchContactHeaderRowBinding headerBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.search_contact_header_row, parent, false);
                return new SearchContactSectionViewHolder(context, headerBinding);
            case 1:
                SearchContactDataRowBinding rowBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.search_contact_data_row, parent, false);
                return new SearchContactRowViewHolder(context, rowBinding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SearchableContactData dataAtPos = mFilterList.get(position);
        if (dataAtPos.isHeader()) {
            SearchContactSectionViewHolder section = (SearchContactSectionViewHolder) holder;
            section.setVariable(dataAtPos, mLanguage);
        } else {
            SearchContactRowViewHolder row = (SearchContactRowViewHolder) holder;
            row.setVariable(dataAtPos, mLanguage);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDetailFragment(dataAtPos);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        final SearchableContactData dataAtPos = mFilterList.get(position);
        return dataAtPos.isHeader() ? 0 : 1;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }

    public void setLanguage(Language language) {
        mLanguage = language;
    }

    private void showDetailFragment(final SearchableContactData dataAtPos) {
        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.main_content, ContactDetailFragment.newInstance(dataAtPos, mLanguage, null), "SearchContactDetailFragment")
                .addToBackStack("SearchContactDetailFragment")
                .commit();
    }

    public List<SearchableContactData> createSectionList(List<SearchableContactData> contactList) {
        String prev = "";
        SearchableContactData header;
        List<SearchableContactData> all = new ArrayList<>();
        for (SearchableContactData contactData : contactList) {
            String firstChar = contactData.getFirstCharEn();
            if (!firstChar.equalsIgnoreCase(prev)) {
                prev = firstChar;
                header = new SearchableContactData();
                header.setFirstnameEn(firstChar);
                header.setFirstnameTh(firstChar);
                header.setHeader(true);
                all.add(header);
            }

            all.add(contactData);
        }
        return all;
    }

    private void createIndexList(List<SearchableContactData> contactList) {
        mapIndex.clear();
        for (String anAlphabet : SIDE_INDEX_EN) {
            for (int j = 0; j < contactList.size(); j++) {
                SearchableContactData data = contactList.get(j);
                String value = data.getFirstnameEn();
                if (anAlphabet.equals(value)) {
                    mapIndex.put(value, j);
                }
            }
        }
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private List<SearchableContactData> createFilterList(String filterString) {
        List<SearchableContactData> nlist = new ArrayList<>();
        for (SearchableContactData data : mOriginalList) {
            if (data.regionMatches(filterString)) {
                nlist.add(data);
            }
        }

        return nlist;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults results = new FilterResults();
            String filterString = constraint.toString();
            List<SearchableContactData> filterlist = createFilterList(filterString);
            results.values = filterlist;
            results.count = filterlist.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<SearchableContactData> filterlist = (List<SearchableContactData>) results.values;
            mFilterList = createSectionList(filterlist);
            createIndexList(mFilterList);
            notifyDataSetChanged();
        }
    }
}