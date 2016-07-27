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

import com.appman.intern.R;
import com.appman.intern.databinding.ContactDataRowBinding;
import com.appman.intern.databinding.ContactHeaderRowBinding;
import com.appman.intern.enums.Language;
import com.appman.intern.fragments.ContactDetailFragment;
import com.appman.intern.models.AppContactData;
import com.appman.intern.models.SearchableContactData;
import com.appman.intern.viewholders.ContactRowViewHolder;
import com.appman.intern.viewholders.ContactSectionViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final List<String> SIDE_INDEX_EN = new ArrayList<>(Arrays.asList(new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"}));

    final List<Integer> PHONE_TYPE_LIST = new ArrayList<>(
            Arrays.asList(
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                    ContactsContract.CommonDataKinds.Phone.TYPE_HOME,
                    ContactsContract.CommonDataKinds.Phone.TYPE_WORK));

    FragmentActivity mActivity;
    List<AppContactData> mOriginalList;
    List<AppContactData> mFilterList;
    LayoutInflater mInflater;
    Map<String, Integer> mapIndex = new HashMap<>();
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

    public void setLanguage(Language language) {
        mLanguage = language;
    }

    private void showDetailFragment(final AppContactData dataAtPos) {
        SearchableContactData searchDate = new SearchableContactData(dataAtPos);
        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.main_content, ContactDetailFragment.newInstance(searchDate, mLanguage), "ContactDetailFragment")
                .setCustomAnimations(R.anim.slide_up,R.anim.slide_down,R.anim.slide_up,R.anim.slide_down)
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
}