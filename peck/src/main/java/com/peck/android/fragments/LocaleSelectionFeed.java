package com.peck.android.fragments;

import android.os.Bundle;

import com.peck.android.R;
import com.peck.android.adapters.LocaleSelectAdapter;
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

//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.frag_locale_select, container, false);
//    }


    @Override
    public Feed<Locale> setUpFeed() {
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
