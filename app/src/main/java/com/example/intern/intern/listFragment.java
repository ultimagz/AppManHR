package com.example.intern.intern;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class listFragment extends Fragment implements View.OnClickListener {

    String[] list = { "Aerith Gainsborough", "Barret Wallace", "Cait Sith"
            , "Cid Highwind", "Cloud Strife", "RedXIII", "Sephiroth"
            , "Tifa Lockhart", "Vincent Valentine", "Yuffie Kisaragi"
            , "ZackFair"};

    ArrayList<String> listDataContact = new ArrayList<>();
    HashSet<String> hSetData = new HashSet<>();
    Map<String, Integer> mapIndex;
    ListView listView;
    View rootView;

    public listFragment() {
        // Required empty public constructor
    }

    public ArrayList<ContactData> section(String[] strName) {
        listDataContact = new ArrayList<>(Arrays.asList(strName));
        //ContactData data = new ContactData("A", true);
        ContactData data;

        String prev = "";
        ArrayList<ContactData> all = new ArrayList<>();
        for (String name : strName) {
            String alpha = name.substring(0, 1).toUpperCase();
            if (!alpha.equalsIgnoreCase(prev)) {
                prev = alpha;
                all.add(new ContactData(alpha, true));
            }

            all.add(data = new ContactData(name, false));
        }

       /* Iterator itr = hSetData.iterator();
        while(itr.hasNext()) {
            listDataContact.add((String) itr.next());
        }
        Collections.sort(listDataContact);
*/

        return all;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        listViewAdapter adapter = new listViewAdapter(getActivity(), section(list));
        listView = (ListView)rootView.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        getIndexList(list);


        displayIndex();


        return rootView;
    }

    private void getIndexList(String[] fruits) {
        mapIndex = new LinkedHashMap<String, Integer>();
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
        List<String> indexList = new ArrayList<String>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getActivity().getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            textView.setText(index);
            textView.setOnClickListener((View.OnClickListener) this);
            indexLayout.addView(textView);
        }
    }



    public void onClick(View view) {
        TextView selectedIndex = (TextView) view;
        listView.setSelection(mapIndex.get(selectedIndex.getText()));
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu_tab, menu);
        return true;
    }

}

