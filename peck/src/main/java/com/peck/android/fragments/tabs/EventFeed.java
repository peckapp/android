package com.peck.android.fragments.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peck.android.R;
import com.peck.android.interfaces.BaseEventFeed;

/**
 * Created by mammothbane on 6/9/2014.
 */

public class EventFeed extends BaseEventFeed {

    private final static String tag = "EventFeed";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_eventfeed, container, false);
    }

    public static int getTabTag() {
        return R.string.tb_events;
    }

    public String tag() {
        return tag;
    }


}
