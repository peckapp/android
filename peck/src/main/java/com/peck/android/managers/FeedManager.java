package com.peck.android.managers;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.DataSource;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.models.DBOperable;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/10/2014.
 */
public abstract class FeedManager<T extends DBOperable & SelfSetup & HasFeedLayout> extends Manager<T> {

    //every FeedManager must be a singleton

    protected FeedAdapter<T> adapter;

    public final static String tag = "FeedManager";

    FeedManager() {

    }

    public FeedManager<T> initialize(final FeedAdapter<T> adapter, DataSource<T> dSource) {
        this.adapter = adapter;
        adapter.setSource(FeedManager.this);

        super.initialize(dSource, new Callback<ArrayList<T>>() {
            @Override
            public void callBack(ArrayList<T> obj) {
            }
        });

        return this;

        //testing

    }

    public void loadFromDatabase(DataSource<T> dataSource) {
        super.loadFromDatabase(dataSource, new Callback<ArrayList<T>>() {
            @Override
            public void callBack(ArrayList<T> obj) {
                adapter.notifyDataSetChanged();
            }
        });
    }


    public void add(T item, final Callback<T> callback) {
        super.add(item, new Callback<T>() {
            @Override
            public void callBack(T obj) {
                callback.callBack(obj);
            }
        });
        adapter.notifyDataSetChanged();

    }


}
