package com.peck.android.fragments.tabs;

import com.peck.android.R;
import com.peck.android.fragments.FeedTab;
import com.peck.android.managers.EventManager;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/9/2014.
 */

public class HomeFeed extends FeedTab<Event> {

    private final static String tag = "EventFeed";
    private final static int resId = R.layout.tab_homefeed;
    private final static int lvId = R.id.lv_events;

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
