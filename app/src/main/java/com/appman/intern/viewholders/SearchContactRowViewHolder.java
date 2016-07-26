package com.appman.intern.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import com.appman.intern.BR;
import com.appman.intern.R;
import com.appman.intern.databinding.ContactDataRowBinding;
import com.appman.intern.databinding.SearchContactDataRowBinding;
import com.appman.intern.enums.Language;
import com.appman.intern.models.AppContactData;
import com.appman.intern.models.SearchableContactData;
import com.bumptech.glide.Glide;

public class SearchContactRowViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;
    private SearchContactDataRowBinding mViewDataBinding;

    public SearchContactRowViewHolder(Context context, SearchContactDataRowBinding viewDataBinding) {
        super(viewDataBinding.getRoot());
        mContext = context;
        mViewDataBinding = viewDataBinding;
    }

    public void setVariable(SearchableContactData value, Language lang) {
        mViewDataBinding.setVariable(BR.viewLanguage, lang);
        mViewDataBinding.setVariable(BR.contactData, value);

        if (!TextUtils.isEmpty(value.getImage())) {
            Glide.with(mContext)
                    .load(Base64.decode(value.getImage(), Base64.DEFAULT))
                    .error(R.drawable.dummy_photo)
                    .into(mViewDataBinding.contactImg);
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mViewDataBinding.getRoot().setOnClickListener(onClickListener);
    }
}
