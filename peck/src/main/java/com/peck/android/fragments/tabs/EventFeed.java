package com.peck.android.fragments.tabs;

import com.peck.android.R;
import com.peck.android.adapters.EventFeedAdapter;
import com.peck.android.database.EventDataSource;
import com.peck.android.database.EventOpenHelper;

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
