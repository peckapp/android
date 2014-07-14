package com.peck.android.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.peck.android.BuildConfig;
import com.peck.android.R;
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.interfaces.Callback;
import com.peck.android.managers.DataHandler;
import com.peck.android.models.DBOperable;
import com.peck.android.policies.FiltrationPolicy;

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
    public static final String SQL_QUERY = "sql query";

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
    private AdapterView.OnItemClickListener listener;
    private SimpleCursorAdapter.ViewBinder viewBinder;
    private String query;
    private Cursor cursor;

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

        str = args.getString(SQL_QUERY, "");
        if (str.length() == 0) throw new IllegalArgumentException("Every feed must have an associated SQL query.");
        query = str;
        DataHandler.getDataSource(tClass).query(query, new Callback<Cursor>() { public void callBack(Cursor obj) { cursor = obj; } });

        int res = args.getInt(LV_RES, -1);
        if (res != -1) listViewRes = res;

        res = args.getInt(LAYOUT_RES, -1);
        if (res != -1) layoutRes = res;

        res = args.getInt(FEED_ITEM_LAYOUT, -1);
        if (BuildConfig.DEBUG && res == -1) throw new IllegalArgumentException(getClass().getSimpleName() + "|" + tClass.getSimpleName() +
                ": You must pass a valid layout identifier into the bundle.");
        listItemRes = res;

    }

    public void setViewBinder(SimpleCursorAdapter.ViewBinder binder) {
        this.viewBinder = binder;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.listener = listener;
    }




    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = inflater.inflate(getLayoutRes(), container, false);

        ((ListView)r.findViewById(getListViewRes())).setAdapter(new SimpleCursorAdapter(getActivity(), listItemRes, cursor, ));

        AdapterView<ListAdapter> v = (AdapterView<ListAdapter>) r.findViewById(getListViewRes());
        v.setOnItemClickListener(listener);
        if (feedAdapter == null) feedAdapter = new FeedAdapter<T>(listItemRes, this, viewAdapter);
        v.setAdapter(feedAdapter);
        return r;

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

