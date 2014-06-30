package com.peck.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.peck.android.PeckApp;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.managers.Manager;
import com.peck.android.models.DBOperable;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/9/2014.
 */
public class FeedAdapter<T extends DBOperable & SelfSetup & HasFeedLayout> extends BaseAdapter {
    private Manager<T> manager;
    private int resourceId;

    public FeedAdapter(int resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public int getCount() {
        return getData().size();
    }

    @Override
    public T getItem(int i) {
        return getData().get(i);
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

    private ArrayList<T> getData() {
        return manager.getData();
    }


    public FeedAdapter<T> removeCompleted() {
        //TODO: implement
        return this;
    }

    public FeedAdapter<T> setSource(Manager<T> manager) {
        this.manager = manager;
        return this;
    }


}