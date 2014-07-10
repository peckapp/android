package com.peck.android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.peck.android.PeckApp;
import com.peck.android.fragments.Feed;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.models.DBOperable;

/**
 * Created by mammothbane on 6/9/2014.
 */
public class FeedAdapter<T extends DBOperable & SelfSetup & HasFeedLayout> extends BaseAdapter {

    private int resourceId;

    @NonNull
    private Feed<T> feed;

    public FeedAdapter(int resourceId, Feed<T> feed) {
        this.resourceId = resourceId;
        this.feed = feed;
    }

    @Override
    public int getCount() {
        return feed.getData().size();
    }

    @Override
    public T getItem(int i) {
        return feed.getData().get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).getLocalId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) PeckApp.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resourceId, null);
        }

        T res = getItem(i);
        res.setUp(view);
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        feed.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FeedAdapter.super.notifyDataSetChanged();
            }
        });
    }
}