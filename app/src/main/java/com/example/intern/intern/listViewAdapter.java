package com.example.intern.intern;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

public class listViewAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<ContactData> strName;



    public listViewAdapter(Context context, ArrayList<ContactData> strName) {
        this.mContext= context;
        this.strName = strName;

    }

    public int getCount() {
        return strName.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater mInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View viewText;
        View viewHead;

            Log.i("listFinal",strName+"");
            Log.i("listFinal",strName.size()+"");

           /* view = mInflater.inflate(R.layout.layout_row, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.textView1);
        TextView headView = (TextView) view.findViewById(R.id.headText);
       *//* ImageView img = (ImageView) view.findViewById(R.id.imageView_round);*/

            viewText = mInflater.inflate(R.layout.layout_row, parent, false);
            viewHead = mInflater.inflate(R.layout.header_list, parent, false);
            TextView textView = (TextView) viewText.findViewById(R.id.textView1);
            TextView headView = (TextView) viewHead.findViewById(R.id.headText);

        ContactData dataAtPos = strName.get(position);
            if (dataAtPos.isHeader()) {
                headView.setText(dataAtPos.getValue());
                /*textView.setText(null);*/
                view = viewHead;
            } else {
                textView.setText(dataAtPos.getValue());
                headView.setText(null);
                view = viewText;
            }


        return view;


        /*if(s.get(position).length()==1){
            headView.setText(s.get(position));
        }else {
            textView.setText(s.get(position));
        }*/
        /*String a;
        String b;
        String temp = "";
        a = strName[position].substring(0, 1);

        if (a.equalsIgnoreCase(temp)) {

        } else {
            headView.setText(a);
            if (a.equalsIgnoreCase(temp)) {
                temp = a;
                //}else{
                //  headView.setText(temp);
            }
           *//*
            b = strName[position].substring(0,1);
            if(b.equalsIgnoreCase(temp)) {

            }else{

            }*//*

            textView.setText(strName[position]);

            return view;
        }

*/

    }
}