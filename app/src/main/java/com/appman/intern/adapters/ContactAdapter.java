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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout., parent, false);
                return new CityViewHolder(view);
            case CityEvent.EVENT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
                return new EventViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public static class CityViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        public CityViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.titleTextView);
        }
    }
}
