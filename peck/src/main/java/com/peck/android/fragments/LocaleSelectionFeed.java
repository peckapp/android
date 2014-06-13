package com.peck.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.peck.android.R;
import com.peck.android.activities.FeedActivity;
import com.peck.android.adapters.EventFeedAdapter;
import com.peck.android.adapters.LocaleSelectAdapter;
import com.peck.android.database.source.EventDataSource;
import com.peck.android.database.source.LocaleDataSource;
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
        super.onCreate(savedInstanceState);


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_locale_select, container, false);
    }


    @Override
    protected Feed<Locale> setUpFeed() {
        if (dataSource == null) {
            dataSource = new LocaleDataSource(getActivity());
        }

        if (feedAdapter == null) {
            feedAdapter = new LocaleSelectAdapter(getActivity(), dataSource);
        }

        return this;
    }



    @Override
    public int getListViewRes() {
        return lvRes;
    }


    @Override
    public Class<? extends Singleton> getManagerClass() {
        return LocaleManager.class;
    }

}
