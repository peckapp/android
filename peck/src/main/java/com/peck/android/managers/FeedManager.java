package com.peck.android.managers;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.DataSource;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

import java.util.Collection;
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


    public synchronized void add(T item, final Callback<T> callback) {
        super.add(item, new Callback<T>() {
            @Override
            public void callBack(T obj) {
                //adapter.notifyDataSetChanged();
                callback.callBack(obj);
            }
        });
    }

    @Override
    public synchronized void add(Collection<T> items, final Callback<Collection<T>> callback) {
        super.add(items, new Callback<Collection<T>>() {
            @Override
            public void callBack(Collection<T> obj) {
                //adapter.notifyDataSetChanged();
                callback.callBack(obj);
            }
        });
    }


}
