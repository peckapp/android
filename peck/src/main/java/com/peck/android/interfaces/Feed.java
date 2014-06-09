package com.peck.android.interfaces;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.factories.GenericFactory;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

/**
 * Created by mammothbane on 6/9/2014.
 */
public abstract class Feed<T extends withLocal & SelfSetup & HasFeedLayout,
        S extends GenericFactory<T>> extends Fragment {

    private Class<S> type;
    protected ArrayList<T> data;
    protected FeedAdapter<T> feedAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.type = (Class<S>)
                ((ParameterizedType)getClass()
                        .getGenericSuperclass())
                        .getActualTypeArguments()[0];

        FeedAdapter<T> tempAdapter = null;

        if (feedAdapter == null) {
            try {
                tempAdapter = new FeedAdapter<T>(getActivity(), (GenericFactory<T>) type.getMethod("getFactory", type).invoke(null, null));
            } catch (Exception e) {
                Log.e(tag(), "You *must* specify a getFactory method on every factory.");
                e.printStackTrace();
            }
        feedAdapter = tempAdapter;
        }
        feedAdapter.load(data); //TODO: loading bar

    }

    public void onResume() {
        feedAdapter.removeCompleted();
    }

    protected abstract String tag();

    public abstract String getTabTag();


}
