package com.peck.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.peck.android.factories.GenericFactory;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.interfaces.WithLocal;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/9/2014.
 */
public abstract class FeedAdapter<model extends WithLocal & SelfSetup & HasFeedLayout> extends BaseAdapter {
    private ArrayList<model> data = new ArrayList<model>();
    private Context context;
    private int resourceId;

    public FeedAdapter(Context context, GenericFactory<model> factory) {
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

        model res = (model)getItem(i);
        res.setUp(view);
        return view;
    }



    public FeedAdapter<model> removeCompleted() {
        //TODO: implement
        return this;
    }

    public FeedAdapter<model> update(ArrayList<model> data) {
        this.data = data;
        return this;
    }


}