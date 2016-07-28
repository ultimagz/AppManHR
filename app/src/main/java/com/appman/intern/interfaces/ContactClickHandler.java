package com.appman.intern.interfaces;

import com.appman.intern.enums.Language;
import com.appman.intern.models.AppContactData;

import de.hdodenhof.circleimageview.CircleImageView;

public interface ContactClickHandler {
    void onContactClick(CircleImageView imageView, AppContactData dataAtPos, Language language);
}
