package com.appman.intern;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ContactsFragment extends Fragment implements View.OnClickListener {

    String[] list = { "Aerith Gainsborough", "Barret Wallace", "Cait Sith"
            , "Cid Highwind", "Cloud Strife", "RedXIII", "Sephiroth"
            , "Tifa Lockhart", "Vincent Valentine", "Yuffie Kisaragi"
            , "ZackFair"};

    ArrayList<String> listDataContact = new ArrayList<>();
    HashSet<String> hSetData = new HashSet<>();
    Map<String, Integer> mapIndex;
    ListView listView;
    ListViewAdapter adapter;
    View rootView;

    public static ContactsFragment newInstance(Bundle args) {
        ContactsFragment fragment = new ContactsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public List<ContactData> createContactsList(String[] strName) {
        String prev = "";
        ArrayList<ContactData> all = new ArrayList<>();
        for (String name : strName) {
            String alpha = name.substring(0, 1).toUpperCase();
            if (!alpha.equalsIgnoreCase(prev)) {
                prev = alpha;
                all.add(new ContactData(alpha, true));
            }

            all.add(new ContactData(name, false));
        }

        Log.w("All contacts", all.toString());
        return all;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        adapter = new ListViewAdapter(getActivity(), createContactsList(list));
        listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        getIndexList();
        displayIndex();

        return rootView;
    }

    private void getIndexList() {
        mapIndex = new LinkedHashMap<>();
        for (int i = 0; i < list.length; i++) {
            String fruit = list[i];
            String index = fruit.substring(0, 1);

            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }
    }

    private void displayIndex() {
        LinearLayout indexLayout = (LinearLayout) rootView.findViewById(R.id.side_index);
        TextView textView;
        List<String> indexList = new ArrayList<>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.side_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(this);
            indexLayout.addView(textView);
        }
    }

    public void onClick(View view) {
        TextView selectedIndex = (TextView) view;
        listView.setSelection(mapIndex.get(selectedIndex.getText()));
    }
}

