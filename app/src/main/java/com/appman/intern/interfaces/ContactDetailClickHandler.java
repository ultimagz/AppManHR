package com.appman.intern.interfaces;

import android.view.View;

import com.appman.intern.models.ContactDetailRowModel;

public interface ContactDetailClickHandler {
    void onClick(View view, ContactDetailRowModel dataModel);
}
