package com.peck.android.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.widget.SimpleCursorAdapter;

import com.peck.android.fragments.Feed;
import com.peck.android.models.DBOperable;

/**
 * Created by mammothbane on 6/9/2014.
 */
public class FeedAdapter<T extends DBOperable> extends SimpleCursorAdapter {

    @NonNull
    private ViewAdapter<T> viewAdapter;

    @NonNull
    private Feed<T> feed;

    public FeedAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags, @NonNull Feed<T> feed, @NonNull ViewAdapter<T> viewAdapter) {
        super(context, layout, c, from, to, flags);
        this.feed = feed;
        this.viewAdapter = viewAdapter;
    }

}