package com.appman.intern.adapters;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.appman.intern.R;
import com.appman.intern.models.ContactData;
import com.appman.intern.models.DataModel;
import com.appman.intern.models.PhoneData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class ContactListAdapter extends ArrayAdapter<ContactData> {
    List<Integer> PHONE_TYPE_LIST = new ArrayList<>(
            Arrays.asList(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
            ContactsContract.CommonDataKinds.Phone.TYPE_HOME,
            ContactsContract.CommonDataKinds.Phone.TYPE_WORK));

    Context mContext;
    List<ContactData> mContactList;
    LayoutInflater mInflater;

    public ContactListAdapter(Context context, List<ContactData> contactList) {
        super(context, 0);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mContactList = contactList;
    }

    public int getCount() {
        return mContactList.size();
    }

    public ContactData getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final ContactData dataAtPos = mContactList.get(position);
        return dataAtPos.isHeader() ? createSessionView(dataAtPos, parent) : createContactView(dataAtPos, parent);
    }

    private View createSessionView(ContactData dataAtPos, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.header_list, parent, false);
        TextView headView = (TextView) view.findViewById(R.id.headText);
        headView.setText(dataAtPos.getValue());
        return view;
    }

    private View createContactView(final ContactData dataAtPos, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.layout_row, parent, false);
        TextView title = (TextView) view.findViewById(R.id.contact_title);
        TextView phoneNo = (TextView) view.findViewById(R.id.contact_phone_no);

        title.setText(dataAtPos.getValue());
        List<PhoneData> phoneList = dataAtPos.getPhoneList();
        if (phoneList.size() > 0) {
            phoneNo.setText(phoneList.get(0).getPhoneNo());
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String where =
                        ContactsContract.Contacts.Data._ID + " = ? AND " +
                        ContactsContract.Contacts.Data.MIMETYPE + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = ? ";
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

                    mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    Toast.makeText(mContext, "Update success", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("Update contact failed", String.valueOf(dataAtPos), e);
                    Toast.makeText(mContext, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
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
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "081 123 1234")
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, checkType)
                .build();
    }

    private DataModel containPhoneType(ContactData dataAtPos, int phoneType) {
        String rawContactId = dataAtPos.getId();

        if (TextUtils.isEmpty(rawContactId)) {
            return null;
        } else {
            Cursor cursor = mContext.getContentResolver().query(
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
}