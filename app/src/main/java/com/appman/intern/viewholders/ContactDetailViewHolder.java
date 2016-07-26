package com.appman.intern.viewholders;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

import com.appman.intern.BR;
import com.appman.intern.interfaces.ContactDetailClickHandler;
import com.appman.intern.models.ContactDetailRowModel;

public class ContactDetailViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;
    private ViewDataBinding mViewDataBinding;
    private ContactDetailClickHandler mClickHandler;

    public ContactDetailViewHolder(Context context, ViewDataBinding viewDataBinding, ContactDetailClickHandler clickHandler) {
        super(viewDataBinding.getRoot());
        mContext = context;
        mViewDataBinding = viewDataBinding;
        mClickHandler = clickHandler;
    }

    public void setVariable(int variableId, ContactDetailRowModel value) {
        mViewDataBinding.setVariable(variableId, value);
        mViewDataBinding.setVariable(BR.clickHandler, mClickHandler);
    }
}
