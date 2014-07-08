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
    protected ArrayList<Feed<T>> activeFeeds = new ArrayList<Feed<T>>();

    public final static String tag = "FeedManager";

    FeedManager() {

    }

    public void registerFeed(Feed<T> activeFeed) {
        activeFeeds.add(activeFeed);
    }

    public void deregisterFeed(Feed<T> activeFeed) { if (activeFeeds.contains(activeFeed)) activeFeeds.remove(activeFeed); }

    @Override
    public void downloadFromServer(final Callback<ArrayList<T>> callback) {
        super.downloadFromServer(new Callback<ArrayList<T>>() {
            @Override
            public void callBack(ArrayList<T> obj) {
                callback.callBack(obj);
                notifyDatasetChanged();
            }
        });
    }

    @Override
    public void loadFromDatabase(final Callback<ArrayList<T>> callback) {
        super.loadFromDatabase(new Callback<ArrayList<T>>() {
            @Override
            public void callBack(ArrayList<T> obj) {
                callback.callBack(obj);
                notifyDatasetChanged();
            }
        });
    }


    @Override
    public void addNew(T item) {
        super.addNew(item);
        notifyDatasetChanged();
    }

    @Override
    public void addFromNetwork(T item) {
        super.addFromNetwork(item);
        notifyDatasetChanged();
    }

    @Override
    public void updateFromUser(T item) {
        super.updateFromUser(item);
        notifyDatasetChanged();
    }

    private void notifyDatasetChanged() {
        dSource.post(new Callback() {
            @Override
            public void callBack(Object obj) {
                for (Feed<T> feed : activeFeeds) {
                    feed.notifyDatasetChanged();
                }
            }
        });
    }

}
