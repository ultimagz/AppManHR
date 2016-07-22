package com.appman.intern.viewholders;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appman.intern.BR;
import com.appman.intern.R;
import com.appman.intern.interfaces.ContactDetailClickHandler;
import com.appman.intern.models.ContactDetailRowModel;

import timber.log.Timber;

public class ContactDetailViewHolder extends RecyclerView.ViewHolder implements ContactDetailClickHandler {
    private ViewDataBinding mViewDataBinding;

    public ContactDetailViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding.getRoot());
        mViewDataBinding = viewDataBinding;
    }

    public void setVariable(int variableId, ContactDetailRowModel value) {
        mViewDataBinding.setVariable(variableId, value);
        mViewDataBinding.setVariable(BR.clickHandler, this);
    }

    @Override
    public void onClick(View view, ContactDetailRowModel dataModel) {
        // TODO Implement click action

        switch (view.getId()) {
            case R.id.contact_phone_msg:
                Timber.w("Msg %s", dataModel.getData());
                break;
            case R.id.contact_phone_call:
                Timber.w("Call %s", dataModel.getData());
                break;
            case R.id.contact_email_btn:
                Timber.w("E-Mail %s", dataModel.getData());
                break;
            case R.id.contact_line_btn:
                Timber.w("LINE %s", dataModel.getData());
                break;
        }
    }
}
