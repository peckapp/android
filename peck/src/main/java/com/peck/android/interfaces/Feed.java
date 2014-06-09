package com.peck.android.interfaces;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.factories.GenericFactory;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/9/2014.
 */
public abstract class Feed<T extends withLocal & SelfSetup & HasFeedLayout,
        S extends GenericFactory<T>> extends Fragment {

    protected ArrayList<T> data;
    protected FeedAdapter<T> feedAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (feedAdapter == null) feedAdapter = new FeedAdapter<T>(this, S.getFactory());

        feedAdapter.load(data); //TODO: loading bar

    }

    public void onResume() {
        feedAdapter.removeCompleted();
    }

    public abstract String getTabTag();


}
