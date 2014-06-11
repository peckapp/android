package com.peck.android.fragments.tabs;

import android.os.Bundle;

import com.peck.android.R;
import com.peck.android.interfaces.HasTabTag;
import com.peck.android.managers.EventManager;

/**
 * Created by mammothbane on 6/9/2014.
 */

public class EventFeed extends BaseEventFeed implements HasTabTag {

    private final static String tag = "EventFeed";
    private final static int resId = R.layout.frag_eventfeed;
    private final static int lvId = R.id.lv_events;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public int getLayoutRes() {
        return resId;
    }

    public int getTabTag() {
        return R.string.tb_events;
    }

    public String tag() {
        return tag;
    }

    public int getListViewRes() {
        return lvId;
    }

    public Class getManagerClass() { return EventManager.class; }



}
