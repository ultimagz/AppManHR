package com.appman.intern;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ContactListAdapter extends ArrayAdapter<ContactData> {
    Context mContext;
    List<ContactData> mContactList;

    public ContactListAdapter(Context context, List<ContactData> contactList) {
        super(context, 0);
        mContext= context;
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
        LayoutInflater mInflater = LayoutInflater.from(mContext);

        View viewText = mInflater.inflate(R.layout.layout_row, parent, false);
        View viewHead = mInflater.inflate(R.layout.header_list, parent, false);

        TextView textView = (TextView) viewText.findViewById(R.id.textView1);
        TextView headView = (TextView) viewHead.findViewById(R.id.headText);

        final ContactData dataAtPos = mContactList.get(position);
            if (dataAtPos.isHeader()) {
                headView.setText(dataAtPos.getValue());
                view = viewHead;
            } else {
                textView.setText(dataAtPos.getValue());
                headView.setText(null);
                view = viewText;
            }


        return view;
    }
}