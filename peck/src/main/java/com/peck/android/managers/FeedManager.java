package com.peck.android.managers;

import android.support.annotation.NonNull;

import com.peck.android.fragments.Feed;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.models.DBOperable;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/10/2014.
 */
public abstract class FeedManager<T extends DBOperable & SelfSetup & HasFeedLayout> extends Manager<T> {

    //every FeedManager must be a singleton

    @NonNull
    protected Feed<T> activeFeed;

    public final static String tag = "FeedManager";

    FeedManager() {

    }

    public void setActiveFeed(Feed<T> activeFeed) {
        this.activeFeed = activeFeed;
    }


    @Override
    public void loadFromDatabase(final Callback<ArrayList<T>> callback) {
        super.loadFromDatabase(new Callback<ArrayList<T>>() {
            @Override
            public void callBack(ArrayList<T> obj) {
                if (activeFeed != null) activeFeed.notifyDatasetChanged();
                callback.callBack(obj);
            }
        });
    }


    @Override
    public void addNew() {
        super.addNew();
        if (activeFeed != null) activeFeed.notifyDatasetChanged();
    }

    @Override
    public void addFromNetwork(T item) {
        super.addFromNetwork(item);
        if (activeFeed != null) activeFeed.notifyDatasetChanged();
    }

    @Override
    public void update(T item) {
        super.update(item);
        if (activeFeed != null) activeFeed.notifyDatasetChanged();
    }
}
