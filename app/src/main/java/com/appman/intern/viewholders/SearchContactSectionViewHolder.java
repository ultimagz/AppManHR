package com.appman.intern.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.appman.intern.BR;
import com.appman.intern.databinding.SearchContactHeaderRowBinding;
import com.appman.intern.enums.Language;
import com.appman.intern.models.SearchableContactData;

public class SearchContactSectionViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;
    private SearchContactHeaderRowBinding mViewDataBinding;

    public SearchContactSectionViewHolder(Context context, SearchContactHeaderRowBinding viewDataBinding) {
        super(viewDataBinding.getRoot());
        mContext = context;
        mViewDataBinding = viewDataBinding;
    }

    public void setVariable(SearchableContactData value, Language lang) {
        mViewDataBinding.setVariable(BR.viewLanguage, lang);
        mViewDataBinding.setVariable(BR.contactData, value);
    }
}
