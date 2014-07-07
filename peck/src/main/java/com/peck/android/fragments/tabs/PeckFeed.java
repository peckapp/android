package com.peck.android.fragments.tabs;

import android.os.Bundle;

import com.peck.android.R;
import com.peck.android.fragments.FeedTab;
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
