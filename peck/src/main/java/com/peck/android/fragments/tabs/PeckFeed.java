package com.peck.android.fragments.tabs;

import android.os.Bundle;

import com.peck.android.R;
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.DataSource;
import com.peck.android.database.dataspec.PeckDataSpec;
import com.peck.android.fragments.Feed;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.PeckManager;
import com.peck.android.models.Peck;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class PeckFeed extends FeedTab<Peck> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public Feed<Peck> setUpFeed() {
        if (dataSource == null) {
            dataSource = new DataSource<Peck>(PeckDataSpec.getInstance());
        }

        if (feedAdapter == null) {
            feedAdapter = new FeedAdapter<Peck>(dataSource.generate().getResourceId());
        }

        return this;
    }

    @Override
    public int getListViewRes() {
        return R.id.lv_pecks;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.tab_peckfeed;
    }

    @Override
    public Class<? extends Singleton> getManagerClass() {
        return PeckManager.class;
    }

}
