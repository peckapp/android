package com.peck.android.activities;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import com.peck.android.R;
import com.peck.android.fragments.tabs.DiningFeed;
import com.peck.android.fragments.tabs.EventFeed;
import com.peck.android.fragments.tabs.NewsFeed;
import com.peck.android.interfaces.Feed;

public class FeedActivity extends FragmentActivity {

    private final static Class[] tabs = {EventFeed.class, NewsFeed.class, DiningFeed.class};
    private final static String TAG = "FeedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_root);

        FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.realcontent);

        for (Class i : tabs) {
            String k = "";
            try {
                k = getResources().getString((Integer)(i.getMethod("getTabTag", null).invoke(null, null)));
            } catch (Exception e) {
                Log.e(TAG, "Every feed must implement getTabTag");
                e.printStackTrace();
            }
            tabHost.addTab(tabHost.newTabSpec(k).setIndicator(k), i, null);

            //TODO: tabHost.setOnTabChangedListener

        }



    }



}
