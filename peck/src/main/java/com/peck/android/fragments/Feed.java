package com.peck.android.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.peck.android.BuildConfig;
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

    public static final String CLASS_NAME = "class name";
    protected String tag() {
        return ((Object)this).getClass().getName();
    }
    private final Object dataLock = new Object();
    protected FeedAdapter<T> feedAdapter;
    protected ArrayList<T> data = new ArrayList<T>();
    private FiltrationPolicy<T> filtrationPolicy;
    private Class<T> tClass;
    protected int listViewRes = R.id.lv_content;
    protected int layoutRes = R.layout.feed;
    protected boolean listening;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        String str = args.getString(CLASS_NAME);
        try {
            if (BuildConfig.DEBUG && str == null) throw new RuntimeException("You MUST include a fully qualified class in the bundle.");
            tClass = (Class<T>)Class.forName(str);
            if (BuildConfig.DEBUG && !DBOperable.class.isAssignableFrom(tClass)) throw new ClassCastException("tClass was not assignable from " + str);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("tClass wasn't assignable from " + str + ". Class name should be fully qualified, of the form 'com.peck.android.fragments.Feed'.");
        }

        try {
            feedAdapter = new FeedAdapter<T>(tClass.newInstance().getResourceId(), this);
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
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
        feedAdapter.notifyDataSetChanged();
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
        DataHandler.register(getParameterizedClass(), this);

        listening = true;
        DataHandler.init(getParameterizedClass());

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
    public void receive(T t) {
        if ((filtrationPolicy != null && filtrationPolicy.test(t)) || filtrationPolicy == null) {
            synchronized (dataLock) {
                if (!data.contains(t)) data.add(t);
                else if (!data.get(data.indexOf(t)).getUpdated().before(t.getUpdated())) {
                    data.remove(t);
                    data.add(t);
                } else {
                    DataHandler.put(tClass, data.get(data.indexOf(t)), false);
                }
            }
        }

        feedAdapter.notifyDataSetChanged();
    }

    public void onPause() {
        DataHandler.unregister(getParameterizedClass(), this);
        super.onPause();
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

