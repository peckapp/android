package com.peck.android.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.peck.android.R;
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.managers.DataHandler;
import com.peck.android.models.DBOperable;
import com.peck.android.policies.FiltrationPolicy;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/9/2014.
 *
 * class to handle feed fragments
 *
 */

public class Feed<T extends DBOperable & SelfSetup & HasFeedLayout> extends Fragment {

    protected String tag() {
        return ((Object)this).getClass().getName();
    }
    protected FeedAdapter<T> feedAdapter;
    protected ArrayList<T> data = new ArrayList<T>();
    private FiltrationPolicy<T> filtrationPolicy;
    private Class<T> tClass;
    protected int listViewRes = R.id.lv_content;
    protected int layoutRes = R.layout.feed;
    protected boolean listening;

    {
        /*try {


            feedAdapter = new FeedAdapter<T>(((T)((Class)TypeResolver.resolveGenericType(Feed.class, getClass())).newInstance()).getResourceId(), this);
            feedAdapter = new FeedAdapter<T>(((T)TypeResolver.resolveRawArgument(Feed.class, getClass()).newInstance()).getResourceId(), this);
        } catch (Exception e) {
            throw new RuntimeException("couldn't instantiate an object");
        }*/
    }


    public void setUp(Class<T> tClass) {
        this.tClass = tClass;
        try {
            feedAdapter = new FeedAdapter<T>(tClass.newInstance().getResourceId(), this);
        } catch (Exception e) {
            throw new RuntimeException("couldn't instantiate an object");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //todo: ensure manager has data loaded
        super.onCreate(savedInstanceState);
        DataHandler.register(getParameterizedClass(), this);

    }

    @Override
    public void onStart() {
        super.onStart();
        listening = true;
        DataHandler.init(getParameterizedClass());

    }


    @Subscribe
    public void initComplete(DataHandler.InitComplete complete) {
        if (!listening) return;
        switch (DataHandler.getLoadState(getParameterizedClass()).getValue()) {
            //todo: handle these items differently

            case DB_LOADED:
                data = DataHandler.getData(getParameterizedClass());
                break;
            case LOAD_COMPLETE:
                data = DataHandler.getData(getParameterizedClass());
                break;
            case NOT_LOADED:
                //todo: throw an error
                break;
        }
        listening = false;
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

    @NonNull
    @SuppressWarnings("unchecked")
    public Class<T> getParameterizedClass() {
        if (tClass == null) throw new RuntimeException("tClass must be set before the Feed is instantiated.");
        return tClass;
    }

}

