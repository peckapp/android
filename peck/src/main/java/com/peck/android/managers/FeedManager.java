package com.peck.android.managers;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.DataSource;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

import java.util.ArrayList;

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
            }
        });
        this.adapter = adapter;
        adapter.setSource(FeedManager.this);

        T t;
        for (int i = 1; i < 21; i++) {
            t = dSource.generate();
            add(t, new Callback<T>() {
                public void callBack(T obj) {
                }
            });
        }
        return this;

        //testing

    }

    public ArrayList<T> loadFromDatabase(DataSource<T> dataSource) {
        return super.loadFromDatabase(dataSource, new Callback() {
            @Override
            public void callBack(Object obj) {
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
