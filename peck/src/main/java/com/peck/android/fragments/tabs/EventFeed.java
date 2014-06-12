package com.peck.android.fragments.tabs;

import android.os.Bundle;

import com.peck.android.R;
import com.peck.android.interfaces.HasTabTag;
import com.peck.android.managers.EventManager;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/9/2014.
 */

public class EventFeed extends BaseEventFeed implements HasTabTag {

    private final static String tag = "EventFeed";
    private final static int resId = R.layout.frag_eventfeed;
    private final static int lvId = R.id.lv_events;
    private final static int tabId = R.string.tb_events;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Event e;
        for (int i = 1; i < 21; i++) {
            e = new Event();
            e.setServerId(i);
            e.setTitle("Event " + Integer.toString(i));
            e.setText("Text " + Integer.toString(i));
            feedManager.add(e);
        }

    }

    public int getLayoutRes() {
        return resId;
    }

    public int getTabTag() {
        return tabId;
    }

    public String tag() {
        return tag;
    }

    public int getListViewRes() {
        return lvId;
    }

    public Class<EventManager> getManagerClass() { return EventManager.class; }

}
