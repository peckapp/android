package com.peck.android.fragments.tabs;


import com.peck.android.R;

/**
 * Created by mammothbane on 6/9/2014.
 */
public class NewsFeed extends BaseEventFeed {

    private static final String tag = "NewsFeed";
    private static final int resId = R.layout.frag_newsfeed;

    public int getLayoutRes() {
        return resId;
    }

    public int getTabTag() {
        return R.string.tb_newsfeed;
    }

    public String tag() {
        return tag;
    }

}
