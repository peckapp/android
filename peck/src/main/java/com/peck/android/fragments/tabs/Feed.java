package com.peck.android.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.source.DataSource;
import com.peck.android.database.helper.DataSourceHelper;
import com.peck.android.factories.GenericFactory;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.HasTabTag;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.interfaces.WithLocal;
import com.peck.android.managers.ModelManager;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/9/2014.
 */
public abstract class Feed<T extends WithLocal & SelfSetup & HasFeedLayout,
        S extends GenericFactory<T>, V extends DataSourceHelper<T>> extends Fragment implements HasTabTag {

    protected FeedAdapter<T> feedAdapter;
    protected DataSource<T, V> dataSource;
    protected ModelManager<T, V> modelManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpAdapter();

        modelManager = new ModelManager<T, V>();
        modelManager.initialize(feedAdapter, dataSource); //TODO: loading bar

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

