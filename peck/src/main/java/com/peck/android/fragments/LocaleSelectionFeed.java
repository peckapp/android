package com.peck.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.peck.android.R;
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.DataSource;
import com.peck.android.database.dataspec.LocaleDataSpec;
import com.peck.android.interfaces.Singleton;
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
                        LocaleManager.getManager().setLocale((Locale) feedAdapter.getItem(i));
                        getActivity().finish();
                    }
                });

        return view;
    }




    @Override
    public Feed<Locale> setUpFeed() {
        if (dataSource == null) {
            dataSource = new DataSource<Locale>(LocaleDataSpec.getHelper());
        }

        if (feedAdapter == null) {
            feedAdapter = new FeedAdapter<Locale>(getActivity(), dataSource);
        }

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
