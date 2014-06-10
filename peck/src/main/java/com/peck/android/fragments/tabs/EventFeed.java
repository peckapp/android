package com.peck.android.fragments.tabs;

import com.peck.android.R;

/**
 * Created by mammothbane on 6/9/2014.
 */

public class EventFeed extends BaseEventFeed {

    private final static String tag = "EventFeed";
    private final static int resId = R.layout.frag_eventfeed;

    public int getLayoutRes() {
        return resId;
    }

    public static int getTabTag() {
        return R.string.tb_events;
    }

    public String tag() {
        return tag;
    }



}
