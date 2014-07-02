package com.peck.android.managers;

import android.support.annotation.NonNull;

import com.peck.android.adapters.FeedAdapter;
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
    protected FeedAdapter<T> adapter = new FeedAdapter<T>(dSource.generate().getResourceId());

    @NonNull
    protected Feed<T> feed;

    public final static String tag = "FeedManager";

    FeedManager() {

    }

    public FeedManager<T> initialize(final Feed<T> feed, Callback<ArrayList<T>> callback) {
        this.feed = feed;
        this.adapter = feed.getAdapter();
        adapter.setSource(FeedManager.this);

        super.initialize(callback);

        return this;

        //testing

    }

    @Override
    public void loadFromDatabase(Callback<ArrayList<T>> callback) {
        super.loadFromDatabase(new Callback<ArrayList<T>>() {
            @Override
            public void callBack(ArrayList<T> obj) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }



    public void addNetwork(T item, final Callback<T> callback) {
        super.addNetwork(item);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }});
    }


    /**
     * convenience method to run code in the ui thread
     *
     * @param runnable a runnable to execute in the ui thread
     */

    public void runOnUiThread(Runnable runnable) {
        feed.getActivity().runOnUiThread(runnable);
    }

}
