package com.appman.intern.adapters;

import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.appman.intern.R;
import com.appman.intern.enums.Language;
import com.appman.intern.fragments.ContactDetailFragment;
import com.appman.intern.models.AppContactData;
import com.appman.intern.models.ContactData;
import com.appman.intern.models.DataModel;
import com.appman.intern.models.PhoneData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ContactListAdapter extends ArrayAdapter<ContactData> {
    List<Integer> PHONE_TYPE_LIST = new ArrayList<>(
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
        super(activity, 0);
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        mOriginalList = new ArrayList<>(contactList);
        mFilterList = createSectionList(contactList);
        createIndexList(mFilterList);
    }

    public int getCount() {
        return mFilterList.size();
    }

    public ContactData getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final AppContactData dataAtPos = mFilterList.get(position);
        view = dataAtPos.isHeader() ? createSessionView(dataAtPos, parent) : createContactView(dataAtPos, parent);
        return view;
    }

    public void setLanguage(Language language) {
        mLanguage = language;
    }

    private View createSessionView(AppContactData dataAtPos, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.contact_header_row, parent, false);
        TextView headView = (TextView) view.findViewById(R.id.section_title);
        headView.setText(dataAtPos.getFirstCharEn());
        view.setOnClickListener(null);

        return view;
    }

    private View createContactView(final AppContactData dataAtPos, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.contact_data_row, parent, false);
        TextView title = (TextView) view.findViewById(R.id.contact_name);
        TextView phoneNo = (TextView) view.findViewById(R.id.contact_phone_no);

        if (TextUtils.isEmpty(dataAtPos.getNicknameEn())) {
            title.setText(mLanguage == Language.TH ? dataAtPos.getFullNameTh() : dataAtPos.getFullNameEn());
        } else {
            title.setText(String.format("%s (%s)", dataAtPos.getFullNameEn(), dataAtPos.getNicknameEn()));
        }

        phoneNo.setText(dataAtPos.getMobile());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailFragment(dataAtPos);
            }
        });

        return view;
    }

    private void showDetailFragment(final AppContactData dataAtPos) {
        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .add(R.id.main_content, ContactDetailFragment.newInstance(dataAtPos), "ContactDetailFragment")
                .addToBackStack("ContactDetailFragment")
                .commit();
    }

    private void updateContact(final ContactData dataAtPos) {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();

            List<PhoneData> phoneList = dataAtPos.getPhoneList();

            for (int checkType : PHONE_TYPE_LIST) {
                DataModel dataModel = containPhoneType(dataAtPos, checkType);
                if (dataModel == null) {
                    ops.add(createInsertContact(dataAtPos, checkType));
                } else {
                    ops.add(createUpdateContact(dataAtPos, dataModel, checkType));
                }
            }

            mActivity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Toast.makeText(mActivity, "Update success", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Update contact failed", String.valueOf(dataAtPos), e);
            Toast.makeText(mActivity, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

    private ContentProviderOperation createInsertContact(final ContactData dataAtPos, final int checkType) throws RemoteException, OperationApplicationException {
        Timber.w("createInsertContact\n%s", String.valueOf(dataAtPos));
        return ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValue(ContactsContract.Data.RAW_CONTACT_ID, dataAtPos.getRawContactId())
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "089 987 9876")
//                .withValue(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY, dataAtPos.getLookupKey())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, checkType)
                .build();
    }

    private ContentProviderOperation createUpdateContact(final ContactData dataAtPos, final DataModel dataModel, final int checkType) throws RemoteException, OperationApplicationException {
        Timber.w("createUpdateContact\n%s", String.valueOf(dataAtPos));
        return ContentProviderOperation
                .newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(
                        ContactsContract.Data.CONTACT_ID + " = ? AND " +
                                ContactsContract.Data.MIMETYPE + " = ? AND " +
                                ContactsContract.CommonDataKinds.Phone.TYPE + " = ?",
                        new String[]{
                                dataAtPos.getId(),
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                                String.valueOf(checkType)
                        })
                .withValue(
                        ContactsContract.Contacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "081 123 1234")
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, checkType)
                .build();
    }

    private DataModel containPhoneType(ContactData dataAtPos, int phoneType) {
        String rawContactId = dataAtPos.getId();

        if (TextUtils.isEmpty(rawContactId)) {
            return null;
        } else {
            Cursor cursor = mActivity.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + " = ? AND " +
                            ContactsContract.Data.MIMETYPE + " = ? AND " +
                            ContactsContract.CommonDataKinds.Phone.TYPE + " = ?",
                    new String[]{rawContactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, String.valueOf(phoneType)},
                    null);

            DataModel returnData = null;
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                String dataId = cursor.getString(cursor.getColumnIndex(ContactsContract.Data._ID));
                returnData = new DataModel(rawContactId, dataId);
                cursor.close();
            }

            return returnData;
        }
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
        String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

        for (String anAlphabet : alphabet) {
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
        boolean exist;
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