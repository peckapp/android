package com.peck.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.peck.android.R;
import com.peck.android.managers.LocaleManager;
import com.peck.android.models.Locale;

/**
 * Created by mammothbane on 6/13/2014.
 */
public class LocaleSelectionFeed extends Feed<Locale> {

    private final static int lvRes = R.id.lv_locale_select;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ((ListView) view.findViewById(lvRes)).setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        LocaleManager.getManager().setLocale(feedAdapter.getItem(i));
                        getActivity().finish();
                    }
                });

        return view;
    }

    @Override
    public int getListViewRes() {
        return lvRes;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.frag_locale_select;
    }

}
