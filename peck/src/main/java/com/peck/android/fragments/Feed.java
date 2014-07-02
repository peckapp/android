package com.peck.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.DataSource;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.HasManager;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.managers.FeedManager;
import com.peck.android.models.DBOperable;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/9/2014.
 *
 * class to handle feed fragments
 *
 */
public abstract class Feed<T extends DBOperable & SelfSetup & HasFeedLayout> extends BaseTab implements HasManager {

    protected String tag() {
        return ((Object)this).getClass().getName();
    }

    protected FeedAdapter<T> feedAdapter;

    protected DataSource<T> dataSource;

    protected FeedManager<T> feedManager;

    @Nullable
    protected Callback<ArrayList<T>> callback = new Callback<ArrayList<T>>() {
        @Override
        public void callBack(ArrayList<T> obj) {

        }
    };

    public Feed() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        congfigureManager();
        super.onCreate(savedInstanceState);

    }


    public void onStart() {
        super.onStart();

    }

    @SuppressWarnings("unchecked")
    protected void congfigureManager() {
        feedManager = ((FeedManager<T>) FeedManager.getManager(getManagerClass())).initialize(this, callback);
    }

    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = inflater.inflate(getLayoutRes(), container, false);
        AdapterView<ListAdapter> v = (AdapterView<ListAdapter>)r.findViewById(getListViewRes());
        associateAdapter(v);
        return r;
    }

    @SuppressWarnings("unchecked")
    public Feed<T> associateAdapter(AdapterView<ListAdapter> v) {
        v.setAdapter(feedAdapter);
        return this;
    }

    public void onResume() {
        super.onResume();
        feedAdapter.notifyDataSetChanged();
    }

    public FeedAdapter<T> getAdapter() { return feedAdapter; }

    public abstract int getListViewRes();
    public abstract int getLayoutRes();

}

