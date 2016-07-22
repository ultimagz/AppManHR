package com.appman.intern.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appman.intern.R;
import com.appman.intern.models.AppContactData;

import java.util.List;

/**
 * Created by sodajune on 7/21/16.
 */
public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<AppContactData> mList;
    public ContactAdapter(List<AppContactData> list) {

        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case AppContactData.MOBILE_TYPE :
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_detail_row_moblie, parent, false);
                return new MoblieViewHolder(view);
            case AppContactData.WORK_PHONE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_detail_row_workphone, parent, false);
                return new WorkPhoneViewHolder(view);
            case AppContactData.E_MAIL_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_detail_row_e_mail, parent, false);
            return new MailViewHolder(view);
            case AppContactData.ID_LINE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_detail_row_line_id, parent, false);
                return new LineIDViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AppContactData object = mList.get(position);
        if (object != null) {
            switch (object.getType()) {
                case AppContactData.MOBILE_TYPE:
                    ((MoblieViewHolder) holder).mobile.setText(object.getMobile());
                    break;
                case AppContactData.WORK_PHONE_TYPE:
                    ((WorkPhoneViewHolder) holder).workPhone.setText(object.getWorkPhone());
                    break;
                case AppContactData.E_MAIL_TYPE:
                    ((MailViewHolder) holder).email.setText(object.getEmail());
                    break;
                case AppContactData.ID_LINE_TYPE:
                    ((LineIDViewHolder) holder).lineID.setText(object.getLineID());
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {

        if (mList == null)
            return 0;
        return mList.size();
    }
    @Override
    public int getItemViewType(int position) {
        if (mList != null) {
            AppContactData object = mList.get(position);
            if (object != null) {
                return object.getType();
            }
        }
        return 0;
    }


    private class LineIDViewHolder extends RecyclerView.ViewHolder {
        private TextView lineID;
        public LineIDViewHolder(View view) {
            super(view);
            lineID = (TextView) view.findViewById(R.id.lineld);
        }
    }

    private class MoblieViewHolder extends RecyclerView.ViewHolder {
        private TextView mobile;
        public MoblieViewHolder(View view) {
            super(view);
            mobile = (TextView) view.findViewById(R.id.mobile);
        }
    }

    private class MailViewHolder extends RecyclerView.ViewHolder {
        private TextView email;
        public MailViewHolder(View view) {
            super(view);
            email = (TextView) view.findViewById(R.id.email);
        }
    }

    private class WorkPhoneViewHolder extends RecyclerView.ViewHolder {
        private TextView workPhone;
        public WorkPhoneViewHolder(View view) {
            super(view);
            workPhone = (TextView) view.findViewById(R.id.work_phone);
        }
    }
}
