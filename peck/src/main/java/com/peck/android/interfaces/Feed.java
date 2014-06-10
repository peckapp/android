package com.peck.android.interfaces;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.DataSource;
import com.peck.android.database.DataSourceHelper;
import com.peck.android.factories.EventFactory;
import com.peck.android.factories.GenericFactory;
import com.peck.android.fragments.tabs.NewsFeed;
import com.peck.android.models.Event;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

/**
 * Created by mammothbane on 6/9/2014.
 */
public abstract class Feed<T extends WithLocal & SelfSetup & HasFeedLayout,
        S extends GenericFactory<T>, V extends DataSourceHelper<T>> extends Fragment {

    protected ArrayList<T> data;
    protected FeedAdapter<T> feedAdapter;
    protected DataSource<T, V> dataSource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpAdapter();
        feedAdapter.load(data, dataSource); //TODO: loading bar

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutRes(), container, false);
    }

    public void onResume() {
        feedAdapter.removeCompleted();
        super.onResume();
    }

    protected abstract Feed<T, S, V> setUpAdapter(); //should set adapter and datasource

    protected abstract String tag();

    protected abstract GenericFactory<T> getFactory();

    public abstract int getLayoutRes();

}

