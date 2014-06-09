package com.peck.android.activities;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.peck.android.R;
import com.peck.android.fragments.tabs.EventFeed;
import com.peck.android.fragments.tabs.NewsFeed;


public class FeedActivity extends FragmentActivity {

    private final static Class[] tabs = {EventFeed.class, NewsFeed.class};
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
                k = (String)i.getMethod("getTabTag", null).invoke(null, null);
                Log.d(TAG, k);
            } catch (Exception e) { e.printStackTrace(); }
            tabHost.addTab(tabHost.newTabSpec(k).setIndicator(k), i, null);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed_root, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
