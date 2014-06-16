package com.peck.android.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;

import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.Factory;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/9/2014.
 */
public abstract class FeedAdapter<T extends DBOperable & SelfSetup & HasFeedLayout> extends BaseAdapter {
    private ArrayList<T> data = new ArrayList<T>();
    private Context context;
    private int resourceId;

    public FeedAdapter(Context context, Factory<T> factory) {
        this.context = context;
        this.resourceId = factory.generate().getResourceId();
    }

    public Context getContext() { return context; }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return data.get(i).getLocalId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resourceId, null);
        }

        T res = (T)getItem(i);
        res.setUp(view);
        return view;
    }


    public FeedAdapter<T> removeCompleted() {
        //TODO: implement
        return this;
    }

    public FeedAdapter<T> update(ArrayList<T> data) {
        this.data = data;
        return this;
    }


}