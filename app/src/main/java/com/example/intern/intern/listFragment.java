package com.example.intern.intern;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class listFragment extends Fragment {

    String[] list = { "Aerith Gainsborough", "Barret Wallace", "Cait Sith"
            , "Cid Highwind", "Cloud Strife", "RedXIII", "Sephiroth"
            , "Tifa Lockhart", "Vincent Valentine", "Yuffie Kisaragi"
            , "ZackFair"};

    ArrayList<String> listDataContact = new ArrayList<>();
    HashSet<String> hSetData = new HashSet<>();
    

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

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        listViewAdapter adapter = new listViewAdapter(getActivity(), section(list));
        ListView listView = (ListView)rootView.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        return rootView;
    }

}

