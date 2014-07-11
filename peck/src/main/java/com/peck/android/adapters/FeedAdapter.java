package com.peck.android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.peck.android.PeckApp;
import com.peck.android.fragments.Feed;
import com.peck.android.models.DBOperable;

/**
 * Created by mammothbane on 6/9/2014.
 */
public class FeedAdapter<T extends DBOperable> extends BaseAdapter {

    private int resourceId;

    @NonNull
    private ViewAdapter<T> viewAdapter;

    @NonNull
    private Feed<T> feed;

    public FeedAdapter(int resourceId, Feed<T> feed, ViewAdapter<T> viewAdapter) {
        this.resourceId = resourceId;
        this.feed = feed;
        this.viewAdapter = viewAdapter;
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

        viewAdapter.setUp(view, getItem(i));

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