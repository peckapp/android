package com.peck.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.DataSource;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.managers.DataHandler;
import com.peck.android.models.DBOperable;
import com.peck.android.policies.FiltrationPolicy;
import com.squareup.otto.Subscribe;

import net.jodah.typetools.TypeResolver;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/9/2014.
 *
 * class to handle feed fragments
 *
 */

public abstract class Feed<T extends DBOperable & SelfSetup & HasFeedLayout> extends Fragment {

    public final static String LV_ID = "list view identifier";
    public final static String LAYOUT_ID = "layout identifier";

    protected String tag() {
        return ((Object)this).getClass().getName();
    }
    protected FeedAdapter<T> feedAdapter = new FeedAdapter<T>(new DataSource<T>((Class<T>) TypeResolver.resolveRawArgument(Feed.class, getClass())).generate().getResourceId(), this);
    protected ArrayList<T> data = new ArrayList<T>();
    private FiltrationPolicy<T> filtrationPolicy;
    protected int listViewRes;
    protected int layoutRes;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //todo: ensure manager has data loaded

        super.onCreate(savedInstanceState);
        DataHandler.register(getParameterizedClass(), this);

    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        listViewRes = args.getInt(LV_ID, -1);
        layoutRes = args.getInt(LAYOUT_ID, -1);

        if (listViewRes == -1 || layoutRes == -1) throw new IllegalArgumentException("Feed must be instantiated with layout and list view identifiers.");
    }

    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = inflater.inflate(getLayoutRes(), container, false);
        AdapterView<ListAdapter> v = (AdapterView<ListAdapter>)r.findViewById(getListViewRes());
        v.setAdapter(feedAdapter);
        return r;
    }

    public void onResume() {
        super.onResume();
        feedAdapter.notifyDataSetChanged();
    }

    /**
     *
     * made available for feedadapters.
     *
     * @return the arraylist of data this feed handles
     */
    public ArrayList<T> getData() {
        return data;
    }

    @Subscribe
    public void respondTo(T t) {
        if (filtrationPolicy.test(t)) data.add(t);
    }

    public void onDestroy() {
        DataHandler.unregister(getParameterizedClass(), this);
        super.onDestroy();
    }

    public int getListViewRes() {
        return listViewRes;
    }

    public int getLayoutRes() {
        return layoutRes;
    }

    @SuppressWarnings("unchecked")
    public Class<T> getParameterizedClass() {
        return (Class<T>) TypeResolver.resolveRawArgument(Feed.class, getClass());
    }

}

