package com.peck.android.managers;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.DataSource;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mammothbane on 6/10/2014.
 */
public abstract class FeedManager<T extends DBOperable & SelfSetup & HasFeedLayout> extends Manager<T> {

    //every FeedManager **must** implement a static version of getManager/be a singleton

    protected FeedAdapter<T> adapter;

    public final static String tag = "FeedManager";

    FeedManager() {

    }

    public FeedManager<T> initialize(final FeedAdapter<T> adapter, DataSource<T> dSource) {
        super.initialize(dSource, new Callback() {
            @Override
            public void callBack(Object obj) {
                adapter.setSource(FeedManager.this);
            }
        });
        this.adapter = adapter;
        return this;
    }

    public <V extends DBOperable> HashMap<Integer, V> loadFromDatabase(DataSource<V> dataSource) {
        return super.loadFromDatabase(dataSource, new Callback() {
            @Override
            public void callBack(Object obj) {
                adapter.notifyDataSetChanged();
            }
        });
    }


    public synchronized T add(T item) {
        super.add(item);
        adapter.notifyDataSetChanged();
        return item;
    }

    public synchronized ArrayList<T> add(ArrayList<T> items) {
        for (T i : items) {
            super.add(i);
        }
        adapter.notifyDataSetChanged();
        return items;
    }


}
