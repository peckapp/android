package com.peck.android.interfaces;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.peck.android.adapters.FeedAdapter;
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
        S extends GenericFactory<T>> extends Fragment {

    protected ArrayList<T> data;
    protected FeedAdapter<T> feedAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FeedAdapter<T> tempAdapter = null;

        if (feedAdapter == null) {
            feedAdapter = new FeedAdapter<T>(getActivity(), getFactory());
        }
        feedAdapter.load(data); //TODO: loading bar

    }

    public void onResume() {
        feedAdapter.removeCompleted();
        super.onResume();
    }

    protected abstract String tag();

    protected abstract GenericFactory<T> getFactory();


}

