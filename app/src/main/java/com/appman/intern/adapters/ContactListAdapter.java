package com.appman.intern.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.appman.intern.R;
import com.appman.intern.databinding.ContactDataRowBinding;
import com.appman.intern.databinding.ContactHeaderRowBinding;
import com.appman.intern.enums.Language;
import com.appman.intern.fragments.ContactDetailFragment;
import com.appman.intern.models.AppContactData;
import com.appman.intern.viewholders.ContactRowViewHolder;
import com.appman.intern.viewholders.ContactSectionViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    public static final List<String> SIDE_INDEX_EN = new ArrayList<>(Arrays.asList(new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"}));

    final List<Integer> PHONE_TYPE_LIST = new ArrayList<>(
            Arrays.asList(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                    ContactsContract.CommonDataKinds.Phone.TYPE_HOME,
                    ContactsContract.CommonDataKinds.Phone.TYPE_WORK));

    FragmentActivity mActivity;
    List<AppContactData> mOriginalList;
    List<AppContactData> mFilterList;
    LayoutInflater mInflater;
    Map<String, Integer> mapIndex = new HashMap<>();
    ItemFilter mFilter = new ItemFilter();
    Language mLanguage = Language.EN;

    public ContactListAdapter(FragmentActivity activity, List<AppContactData> contactList) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        setList(contactList);
    }

    public void setList(List<AppContactData> contactList) {
        mOriginalList = new ArrayList<>(contactList);
        mFilterList = createSectionList(contactList);
        createIndexList(mFilterList);
        notifyDataSetChanged();
    }

    public AppContactData getItem(int position) {
        if (position >= 0 && position < mFilterList.size())
            return mFilterList.get(position);
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        switch (viewType) {
            case 0:
                ContactHeaderRowBinding headerBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.contact_header_row, parent, false);
                return new ContactSectionViewHolder(context, headerBinding);
            case 1:
                ContactDataRowBinding rowBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.contact_data_row, parent, false);
                return new ContactRowViewHolder(context, rowBinding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final AppContactData dataAtPos = mFilterList.get(position);
        if (dataAtPos.isHeader()) {
            ContactSectionViewHolder section = (ContactSectionViewHolder) holder;
            section.setVariable(dataAtPos, mLanguage);
        } else {
            ContactRowViewHolder row = (ContactRowViewHolder) holder;
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
        final AppContactData dataAtPos = mFilterList.get(position);
        return dataAtPos.isHeader() ? 0 : 1;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }

//    public View getView(int position, View view, ViewGroup parent) {
//        final AppContactData dataAtPos = mFilterList.get(position);
//        view = dataAtPos.isHeader() ? createSessionView(dataAtPos, view, parent) : createContactView(dataAtPos, view, parent);
//        return view;
//    }

    public void setLanguage(Language language) {
        mLanguage = language;
    }
//
//    private View createSessionView(AppContactData dataAtPos, View convertView, ViewGroup parent) {
//        View view = convertView;
//        ContactSectionViewHolder viewHolder;
//
//        if (view == null) {
//            view = mInflater.inflate(R.layout.contact_header_row, parent, false);
//            viewHolder = new ContactSectionViewHolder(view);
//            view.setTag(R.id.section_view_holder, viewHolder);
//        } else {
//            viewHolder = (ContactSectionViewHolder) view.getTag(R.id.section_view_holder);
//        }
//
//        viewHolder.section.setText(dataAtPos.getFirstCharEn());
//        view.setOnClickListener(null);
//
//        return view;
//    }
//
//    private View createContactView(final AppContactData dataAtPos, View convertView, ViewGroup parent) {
//        View view = convertView;
//        ContactRowViewHolder viewHolder;
//        if (view == null) {
//            view = mInflater.inflate(R.layout.contact_data_row, parent, false);
//            viewHolder = new ContactRowViewHolder(view);
//            view.setTag(R.id.row_view_holder, viewHolder);
//        } else {
//            viewHolder = (ContactRowViewHolder) view.getTag(R.id.row_view_holder);
//        }
//
//        viewHolder.title.setText(mLanguage == Language.TH ? dataAtPos.getAllNameTh() : dataAtPos.getAllNameEn());
//        viewHolder.phoneNo.setText(dataAtPos.getMobile());
//
//        String base64 = dataAtPos.getImage();
//        if (base64 != null) {
//            byte[] imgBytes = Base64.decode(base64, Base64.DEFAULT);
//            Glide.with(mActivity).load(imgBytes).into(viewHolder.image);
//        }
//
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDetailFragment(dataAtPos);
//            }
//        });
//
//        return view;
//    }

    private void showDetailFragment(final AppContactData dataAtPos) {
        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.main_content, ContactDetailFragment.newInstance(dataAtPos, mLanguage), "ContactDetailFragment")
                .addToBackStack("ContactDetailFragment")
                .commit();
    }

    public List<AppContactData> createSectionList(List<AppContactData> contactList) {
        String prev = "";
        AppContactData header;
        List<AppContactData> all = new ArrayList<>();
        for (AppContactData contactData : contactList) {
            String firstChar = contactData.getFirstCharEn();
            if (!firstChar.equalsIgnoreCase(prev)) {
                prev = firstChar;
                header = new AppContactData();
                header.setFirstnameEn(firstChar);
                header.setFirstnameTh(firstChar);
                header.setHeader(true);
                all.add(header);
            }

            all.add(contactData);
        }
        return all;
    }

    private void createIndexList(List<AppContactData> contactList) {
        mapIndex.clear();
        for (String anAlphabet : SIDE_INDEX_EN) {
            for (int j = 0; j < contactList.size(); j++) {
                AppContactData data = contactList.get(j);
                String value = data.getFirstnameEn();
                if (anAlphabet.equals(value)) {
                    mapIndex.put(value, j);
                }
            }
        }
    }

    public int getMapIndex(String key) {
        Integer index = mapIndex.get(key);
        return index == null ? -1 : index;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private List<AppContactData> createFilterList(String filterString) {
        List<AppContactData> nlist = new ArrayList<>();
        for (AppContactData data : mOriginalList) {
            if (checkRegionMatches(data, filterString)) {
                nlist.add(data);
            }
        }

        return nlist;
    }

    private boolean checkRegionMatches(AppContactData data, String filterString) {
        int searchLength = filterString.length();
        boolean match;

        match = data.getFirstnameEn().regionMatches(true, 0, filterString, 0, searchLength);
        match |= data.getLastnameEn().regionMatches(true, 0, filterString, 0, searchLength);
        match |= data.getNicknameEn().regionMatches(true, 0, filterString, 0, searchLength);
        match |= data.getFirstnameTh().regionMatches(true, 0, filterString, 0, searchLength);
        match |= data.getLastnameTh().regionMatches(true, 0, filterString, 0, searchLength);
        match |= data.getNicknameTh().regionMatches(true, 0, filterString, 0, searchLength);
        match |= data.getPosition().regionMatches(true, 0, filterString, 0, searchLength);

        return match;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString();
            List<AppContactData> filterlist = createFilterList(filterString);
            mFilterList = createSectionList(filterlist);
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            createIndexList(mFilterList);
            notifyDataSetChanged();
        }
    }
}