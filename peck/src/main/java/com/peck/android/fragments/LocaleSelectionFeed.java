package com.peck.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.peck.android.R;
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.source.LocaleDataSource;
import com.peck.android.fragments.tabs.BaseTab;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.LocaleManager;
import com.peck.android.models.Locale;

/**
 * Created by mammothbane on 6/13/2014.
 */
public class LocaleSelectionFeed extends Feed<Locale> {

    private final static int lvRes = R.id.lv_locale_select;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.getSuper(savedInstanceState);


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_locale_select, container, false);
        ((ListView)view.findViewById(getListViewRes())).setAdapter(LocaleManager.getManager().getAdapter());
        return view;
    }


    @Override
    public Feed<Locale> setUpFeed() {

        return this;
    }

    @Override
    public int getListViewRes() {
        return lvRes;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.frag_locale_select;
    }

    @Override
    public Class<? extends Singleton> getManagerClass() {
        return LocaleManager.class;
    }

    @Override
    public int getTabTag() {
        return 0;
    }
}
