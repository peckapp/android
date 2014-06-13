package com.peck.android.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.ListView;

import com.peck.android.PeckApp;
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.source.DataSource;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.HasManager;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.managers.FeedManager;

import java.util.concurrent.CancellationException;

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
    protected ListView lv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //TODO: loading bar

        getActivity().deleteDatabase(PeckApp.Constants.Database.DATABASE_NAME); //TEST: remove before production

    }


    public void onStart() {
        super.onStart();
        setUpFeed();
        congfigureManager();
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



    public void onResume() {
        if (lv == null) ((ListView)getActivity().findViewById(getListViewRes())).setAdapter(feedAdapter);;
        feedAdapter.removeCompleted();
        super.onResume();
    }

    public FeedAdapter<T> getAdapter() { return feedAdapter; }

    protected abstract Feed<T> setUpFeed(); //set adapter and datasource
    public abstract int getListViewRes();

}

