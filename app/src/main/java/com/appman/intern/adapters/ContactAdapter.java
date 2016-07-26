package com.appman.intern.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.appman.intern.BR;
import com.appman.intern.R;
import com.appman.intern.enums.ContactDetailType;
import com.appman.intern.interfaces.ContactDetailClickHandler;
import com.appman.intern.models.ContactDetailRowModel;
import com.appman.intern.viewholders.ContactDetailViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactDetailViewHolder> {

    private List<ContactDetailRowModel> mList = new ArrayList<>();
    private ContactDetailClickHandler mClickHandler;

    public ContactAdapter(List<ContactDetailRowModel> list, ContactDetailClickHandler clickHandler) {
        mClickHandler = clickHandler;
        setList(list);
    }

    public void setList(List<ContactDetailRowModel> list) {
        mList = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    @Override
    public ContactDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding viewDataBinding;
        Context context = parent.getContext();
        switch (ContactDetailType.values()[viewType]) {
            case MOBILE:
            case WORK_PHONE:
                viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.contact_detail_row_phone, parent, false);
                return new ContactDetailViewHolder(context, viewDataBinding, mClickHandler);
            case E_MAIL:
                viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.contact_detail_row_email, parent, false);
                return new ContactDetailViewHolder(context, viewDataBinding, mClickHandler);
            case LINE:
                viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.contact_detail_row_line, parent, false);
                return new ContactDetailViewHolder(context, viewDataBinding, mClickHandler);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(ContactDetailViewHolder holder, int position) {
        ContactDetailRowModel rowModel = mList.get(position);
        if (rowModel != null) {
            switch (rowModel.getViewType()) {
                case MOBILE:
                case WORK_PHONE:
                    holder.setVariable(BR.contactPhone, rowModel);
                    break;
                case E_MAIL:
                    holder.setVariable(BR.contactEmail, rowModel);
                    break;
                case LINE:
                    holder.setVariable(BR.contactLine, rowModel);
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
            ContactDetailRowModel object = mList.get(position);
            if (object != null) {
                return object.getViewType().ordinal();
            }
        }
        return 0;
    }
}
