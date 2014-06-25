package com.peck.android.fragments.tabs;

import android.os.Bundle;

import com.peck.android.R;
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.DataSource;
import com.peck.android.database.dataspec.CirclesDataSpec;
import com.peck.android.fragments.Feed;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.CircleManager;
import com.peck.android.models.Circle;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class CirclesFeed extends FeedTab<Circle> {

    private static final String tag = "Circles";
    private static final int tagId = R.string.tb_circles;
    private static final int resId = R.layout.tab_circlesfeed;
    private static final int lvId = R.id.lv_circles;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO: set onclicklisteners for list items
        super.onCreate(savedInstanceState);
    }

    public int getTabTag() {
        return tagId;
    }

    @Override
    public Feed<Circle> setUpFeed() {
        if (dataSource == null) {
            dataSource = new DataSource<Circle>(CirclesDataSpec.getInstance());
        }

        if (feedAdapter == null) {
            feedAdapter = new FeedAdapter<Circle>(dataSource.generate().getResourceId());
        }

        return this;
    }

    @Override
    public int getListViewRes() {
        return lvId;
    }

    @Override
    public int getLayoutRes() {
        return resId;
    }

    @Override
    public Class<? extends Singleton> getManagerClass() {
        return CircleManager.class;
    }
}
