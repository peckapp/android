package com.peck.android.activities;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.util.Log;

import com.peck.android.R;
import com.peck.android.fragments.tabs.Circles;
import com.peck.android.fragments.tabs.DiningFeed;
import com.peck.android.fragments.tabs.EventFeed;
import com.peck.android.fragments.tabs.Profile;
import com.peck.android.fragments.tabs.NewsFeed;
import com.peck.android.interfaces.HasTabTag;

public class FeedActivity extends FragmentActivity {

    //private final static Class[] tabs = {EventFeed.class, NewsFeed.class, DiningFeed.class, Circles.class, Profile.class};
    private final static String TAG = "FeedActivity";

    private final static HasTabTag[] tabs = {
            new EventFeed(),
            new NewsFeed(),
            new DiningFeed(),
            new Circles(),
            new Profile()
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_root);

        FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.realcontent);

        String k;
        for (HasTabTag i : tabs) {
            k = getResources().getString(i.getTabTag());
            tabHost.addTab(tabHost.newTabSpec(k).setIndicator(k), i.getClass(), null);
        }

        //TODO: tabHost.setOnTabChangedListener


    }



}
