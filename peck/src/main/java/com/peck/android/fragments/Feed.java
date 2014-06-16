package com.peck.android.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.peck.android.PeckApp;
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.source.DataSource;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.HasManager;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.managers.FeedManager;

/**
 * Created by mammothbane on 6/9/2014.
 */
public abstract class Feed<T extends DBOperable & SelfSetup & HasFeedLayout> extends Fragment implements HasManager {

    protected String tag() {
        return getClass().getName();
    }

    protected FeedAdapter<T> feedAdapter;
    protected DataSource<T> dataSource;
    protected FeedManager<T> feedManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setUpFeed();
        //Log.d(tag(), "adapter " + ((feedAdapter == null) ? "null" : "not null"));
        congfigureManager();
        super.onCreate(savedInstanceState);


        //TODO: loading bar

    }


    public void onStart() {
        super.onStart();

    }

    /**
     * call abstract getManagerClass(), returns subclass's manager.
     * pass manager class to Modelmanager's static method,
     * which uses reflection to invoke the manager's singleton get method.
     * set modelManager to the singleton instance of the modelmanager we want.
     */

    @SuppressWarnings("unchecked")
    protected void congfigureManager() {
        //Log.d(tag(), getManagerClass().getName());
        feedManager = ((FeedManager<T>) FeedManager.getManager(getManagerClass())).initialize(feedAdapter, dataSource);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutRes(), container, false);
        ((ListView)v.findViewById(getListViewRes())).setAdapter(feedAdapter);
        return v;
    }

    public void onResume() {
        //Log.d(tag(), "adapter " + ((feedAdapter == null) ? "null" : "not null"));
        //Log.d(tag(), "activity " + ((getActivity() == null) ? "null" : "not null"));
        super.onResume();
        //assign();
        feedAdapter.removeCompleted();
    }

    public FeedAdapter<T> getAdapter() { return feedAdapter; }

    public abstract Feed<T> setUpFeed(); //set adapter and datasource
    public abstract int getListViewRes();
    public abstract int getLayoutRes();

}

