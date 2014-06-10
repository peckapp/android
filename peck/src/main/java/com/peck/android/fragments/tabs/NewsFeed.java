package com.peck.android.fragments.tabs;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peck.android.R;
import com.peck.android.interfaces.BaseEventFeed;

/**
 * Created by mammothbane on 6/9/2014.
 */
public class NewsFeed extends BaseEventFeed {

    private static final String tag = "NewsFeed";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_newsfeed, container, false);
    }

    public static int getTabTag() {
        return R.string.tb_newsfeed;
    }

    public String tag() {
        return tag;
    }

}
