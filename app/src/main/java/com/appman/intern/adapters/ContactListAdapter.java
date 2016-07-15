package com.appman.intern.adapters;

import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appman.intern.R;
import com.appman.intern.fragments.ContactDetailFragment;
import com.appman.intern.models.AppContactData;
import com.appman.intern.models.ContactData;
import com.appman.intern.models.DataModel;
import com.appman.intern.models.PhoneData;
import com.bumptech.glide.Glide;

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
    Map<String, Integer> mapIndex;
    ItemFilter mFilter = new ItemFilter();

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
        ImageView contactImg = (ImageView) view.findViewById(R.id.contact_img);

        title.setText(dataAtPos.getFullNameEn());
        phoneNo.setText(dataAtPos.getMobile());

        String base64 = "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";
        byte[] data1 = Base64.decode(base64, Base64.DEFAULT);
        Glide.with(mActivity).load(data1).into(contactImg);


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
                        new String[] {
                                dataAtPos.getId(),
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                                String.valueOf(checkType)
                        })
                .withValue(
                        ContactsContract.Contacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "083 312 4860")
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
                    new String[] { rawContactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, String.valueOf(phoneType) },
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
                header.setIsHeader(true);
                all.add(header);
            }

            all.add(contactData);
        }
        return all;
    }

    private void createIndexList(List<AppContactData> contactList) {
        mapIndex = new HashMap<>();
        String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

        for (String anAlphabet : alphabet) {
            for (int j = 0; j < contactList.size(); j++) {
                AppContactData data = contactList.get(j);
                String value = data.getFirstnameEn();
                if (anAlphabet.equals(value)) {
                    mapIndex.put(value, j);
                }
            }}
    }

    public int getMapIndex(String key) {
        Integer index = mapIndex.get(key);
        return index == null ? -1 : index;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString();
            FilterResults results = new FilterResults();
            List<AppContactData> nlist = filterString.length() == 0 ? new ArrayList<>(mOriginalList) : createFilterList(filterString);

            results.values = createSectionList(nlist);
            results.count = nlist.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilterList.clear();
            mFilterList = (List<AppContactData>) results.values;
            createIndexList(mFilterList);
            notifyDataSetChanged();
        }
    }

    private List<AppContactData> createFilterList(String filterString) {
        int lengthFilterString = filterString.length();
        List<AppContactData> nlist = new ArrayList<>();
        String filterableString_FirstnameEn = "";
        String filterableString_LastnameEn = "";
        String filterableString_Position = "";
        for (AppContactData data : mOriginalList) {
            if(lengthFilterString <= data.getFirstnameEn().length()){
                filterableString_FirstnameEn = data.getFirstnameEn().substring(0, lengthFilterString);
            }

            if(lengthFilterString <= data.getLastnameEn().length()){
                filterableString_LastnameEn = data.getLastnameEn().substring(0, lengthFilterString);
            }

            if(lengthFilterString <= data.getPosition().length()){
                filterableString_Position = data.getPosition().substring(0, lengthFilterString);
            }

            if (filterableString_FirstnameEn.equalsIgnoreCase(filterString) || filterableString_LastnameEn.equalsIgnoreCase(filterString) || filterableString_Position.equalsIgnoreCase(filterString)) {
                nlist.add(data);
            }
        }

        return nlist;
    }
}