package com.appman.intern.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.appman.intern.BR;
import com.appman.intern.databinding.ContactHeaderRowBinding;
import com.appman.intern.enums.Language;
import com.appman.intern.models.AppContactData;

public class ContactSectionViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;
    private ContactHeaderRowBinding mViewDataBinding;

    public ContactSectionViewHolder(Context context, ContactHeaderRowBinding viewDataBinding) {
        super(viewDataBinding.getRoot());
        mContext = context;
        mViewDataBinding = viewDataBinding;
    }

    public void setVariable(AppContactData value, Language lang) {
        mViewDataBinding.setVariable(BR.viewLanguage, lang);
        mViewDataBinding.setVariable(BR.contactData, value);
    }
}
