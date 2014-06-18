package com.peck.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mammothbane on 6/17/2014.
 */
public class SimpleFragment extends Fragment {

    public static final String RESOURCE = "resId";
    private int resId;

    @Override
    public void setArguments(Bundle args) {
        resId = args.getInt(RESOURCE, 0);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(resId, container, false);
    }
}
