package com.peck.android.fragments.tabs;

import com.peck.android.R;
import com.peck.android.fragments.BaseEventFeed;
import com.peck.android.managers.EventManager;

/**
 * Created by mammothbane on 6/9/2014.
 */

public class HomeFeed extends BaseEventFeed {

    private final static String tag = "EventFeed";
    private final static int resId = R.layout.tab_homefeed;
    private final static int lvId = R.id.lv_events;
    private final static int tabId = R.string.tb_home;

    public int getLayoutRes() {
        return resId;
    }

    public String tag() {
        return tag;
    }

    public int getListViewRes() {
        return lvId;
    }

    public Class<EventManager> getManagerClass() { return EventManager.class; }

}
