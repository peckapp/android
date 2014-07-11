package com.peck.android.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.peck.android.BuildConfig;
import com.peck.android.R;
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.adapters.ViewAdapter;
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

public class Feed<T extends DBOperable> extends Fragment {

    public static final String CLASS_NAME = "class name";
    public static final String LV_RES = "list view resource identifier";
    public static final String LAYOUT_RES = "layout identifier";
    public static final String FEED_ITEM_LAYOUT = "feed item layout identifier";

    protected String tag() {
        return ((Object)this).getClass().getName();
    }
    protected FeedAdapter<T> feedAdapter;
    protected final ArrayList<T> data = new ArrayList<T>();
    private FiltrationPolicy<T> filtrationPolicy;
    private Class<T> tClass;
    protected int listViewRes = R.id.lv_content;
    protected int layoutRes = R.layout.feed;
    protected int listItemRes;
    private boolean listening;
    private AdapterView.OnItemClickListener listener;
    private ViewAdapter<T> viewAdapter;

    @Override
    @SuppressWarnings("unchecked")
    public void setArguments(Bundle args) {
        super.setArguments(args);
        String str = args.getString(CLASS_NAME);
        try {
            if (BuildConfig.DEBUG && str == null) throw new IllegalArgumentException(getClass().getSimpleName() + "You MUST include a fully qualified class in the bundle.");
            tClass = (Class<T>)Class.forName(str);
            if (BuildConfig.DEBUG && !DBOperable.class.isAssignableFrom(tClass)) throw new IllegalArgumentException("tClass was not assignable from " + str);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(getClass().getSimpleName() + "|" + tClass.getSimpleName() +
                    "tClass wasn't assignable from " + str + ". Class name should be fully qualified, of the form 'com.peck.android.fragments.Feed'.");
        }

        int res = args.getInt(LV_RES, -1);
        if (res != -1) listViewRes = res;

        res = args.getInt(LAYOUT_RES, -1);
        if (res != -1) layoutRes = res;

        res = args.getInt(FEED_ITEM_LAYOUT, -1);
        if (BuildConfig.DEBUG && res == -1) throw new IllegalArgumentException(getClass().getSimpleName() + "|" + tClass.getSimpleName() +
                ": You must pass a valid layout identifier into the bundle.");
        listItemRes = res;

    }

    public void setViewAdapter(@NonNull ViewAdapter<T> viewAdapter) {
        if (BuildConfig.DEBUG && viewAdapter == null) throw new NullPointerException(getClass().getSimpleName() + "|" + tClass.getSimpleName() + ": the ViewAdapter can't be null");
        this.viewAdapter = viewAdapter;
    }

    public void setFiltrationPolicy(@Nullable FiltrationPolicy<T> filtrationPolicy) {
        this.filtrationPolicy = filtrationPolicy;
        if (filtrationPolicy != null) {
            synchronized (data) {
                filtrationPolicy.filter(data);
            }
            if (feedAdapter != null) feedAdapter.notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.listener = listener;
    }

    @Subscribe
    public void initComplete(DataHandler.InitComplete complete) {
        if (!listening) return;
        synchronized (data) {
            switch (DataHandler.getLoadState(getParameterizedClass()).getValue()) {
                //todo: handle these items differently

                case DB_LOADED:
                    data.addAll(DataHandler.getData(getParameterizedClass()));
                    feedAdapter.notifyDataSetChanged();
                    break;
                case LOAD_COMPLETE:
                    data.addAll(DataHandler.getData(getParameterizedClass()));
                    feedAdapter.notifyDataSetChanged();
                    break;
                case NOT_LOADED:
                    //todo: throw an error
                    break;
            }
            listening = false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        synchronized (data) {
            if (feedAdapter == null) feedAdapter = new FeedAdapter<T>(listItemRes, this, viewAdapter);
            View r = inflater.inflate(getLayoutRes(), container, false);
            AdapterView<ListAdapter> v = (AdapterView<ListAdapter>) r.findViewById(getListViewRes());
            v.setAdapter(feedAdapter);
            v.setOnItemClickListener(listener);
            feedAdapter.notifyDataSetChanged();
            return r;
        }
    }

    public void onResume() {
        super.onResume();
        DataHandler.register(getParameterizedClass(), this);

        listening = true;
        DataHandler.init(getParameterizedClass());

    }


    public void onPause() {
        DataHandler.unregister(getParameterizedClass(), this);
        super.onPause();
    }


    @Subscribe
    public void receive(T t) {
        if ((filtrationPolicy != null && filtrationPolicy.test(t)) || filtrationPolicy == null) {
            synchronized (data) {
                if (!data.contains(t)) {
                    data.add(t);
                    feedAdapter.notifyDataSetChanged();
                }
                else if (!data.get(data.indexOf(t)).getUpdated().before(t.getUpdated())) {
                    data.remove(t);
                    data.add(t);
                    feedAdapter.notifyDataSetChanged();
                } else {
                    DataHandler.put(tClass, data.get(data.indexOf(t)), false);
                }
            }
        }

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


    /**
     *
     * made available for feedadapters.
     *
     * @return the arraylist of data this feed handles
     */
    @NonNull
    public ArrayList<T> getData() {
        return data;
    }

}

