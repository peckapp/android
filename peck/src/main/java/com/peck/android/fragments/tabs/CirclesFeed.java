package com.peck.android.fragments.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peck.android.R;
import com.peck.android.adapters.CirclesFeedAdapter;
import com.peck.android.database.source.CirclesDataSource;
import com.peck.android.fragments.Feed;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.CircleManager;
import com.peck.android.models.Circle;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class CirclesFeed extends Feed<Circle> {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_circlesfeed, container, false);
    }

    @Override
    public Feed<Circle> setUpFeed() {
        if (dataSource == null) {
            dataSource = new CirclesDataSource(getActivity());
        }

        if (feedAdapter == null) {
            feedAdapter = new CirclesFeedAdapter(getActivity(), dataSource);
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
