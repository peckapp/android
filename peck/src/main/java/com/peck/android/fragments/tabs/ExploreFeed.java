package com.peck.android.fragments.tabs;


import com.peck.android.R;
import com.peck.android.fragments.BaseEventFeed;
import com.peck.android.managers.EventManager;

/**
 * Created by mammothbane on 6/9/2014.
 */
public class ExploreFeed extends BaseEventFeed {

    private static final String tag = "NewsFeed";
    private static final int resId = R.layout.tab_newsfeed;
    private static final int lvId = R.id.lv_newsfeed;
    private static final int tabId = R.string.tb_newsfeed;



    public int getLayoutRes() {
        return resId;
    }

    public String tag() {
        return tag;
    }

    public int getListViewRes() {
        return lvId;
    }

    public Class<EventManager> getManagerClass() {
        return EventManager.class;
    }

}