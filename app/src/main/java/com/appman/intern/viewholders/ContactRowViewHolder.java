package com.appman.intern.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import com.appman.intern.BR;
import com.appman.intern.R;
import com.appman.intern.databinding.ContactDataRowBinding;
import com.appman.intern.enums.Language;
import com.appman.intern.models.AppContactData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ContactRowViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;
    private ContactDataRowBinding mViewDataBinding;

    public ContactRowViewHolder(Context context, ContactDataRowBinding viewDataBinding) {
        super(viewDataBinding.getRoot());
        mContext = context;
        mViewDataBinding = viewDataBinding;
    }

    public void setVariable(AppContactData value, Language lang) {
        mViewDataBinding.setVariable(BR.viewLanguage, lang);
        mViewDataBinding.setVariable(BR.contactData, value);

        Glide.with(mContext)
                .load(TextUtils.isEmpty(value.getImage()) ? null : Base64.decode(value.getImage(), Base64.DEFAULT))
                .error(R.drawable.dummy_photo)
                .placeholder(R.drawable.dummy_photo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate().dontTransform()
                .into(mViewDataBinding.contactImg);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mViewDataBinding.getRoot().setOnClickListener(onClickListener);
    }
}
