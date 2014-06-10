package com.peck.android.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.peck.android.database.source.DataSource;
import com.peck.android.database.helper.DataSourceHelper;
import com.peck.android.factories.GenericFactory;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.interfaces.WithLocal;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mammothbane on 6/9/2014.
 */
public abstract class FeedAdapter<T extends WithLocal & SelfSetup & HasFeedLayout> extends BaseAdapter {
    private ArrayList<T> objs;
    private GenericFactory factory;
    private Context context;
    private int resourceId;

    public FeedAdapter(Context context, GenericFactory<T> factory) {
        this.context = context;
        this.factory = factory;
        this.resourceId = factory.generate().getResourceId();
    }

    public Context getContext() { return context; }

    @Override
    public int getCount() {
        return objs.size();
    }

    @Override
    public Object getItem(int i) {
        return objs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return objs.get(i).getLocalId();
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

}