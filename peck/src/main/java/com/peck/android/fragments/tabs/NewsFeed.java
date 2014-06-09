package com.peck.android.fragments.tabs;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peck.android.R;

/**
 * Created by mammothbane on 6/9/2014.
 */
public class NewsFeed extends Fragment {

    private final static String TAB_TAG = "News Feed";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_newsfeed, container, false);
    }

    public static String getTabTag() {
        return TAB_TAG;
    }

}
