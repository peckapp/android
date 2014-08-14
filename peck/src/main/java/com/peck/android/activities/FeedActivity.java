/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android.activities;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.peck.android.BuildConfig;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.fragments.NewPostTab;
import com.peck.android.fragments.ProfileTab;
import com.peck.android.fragments.feeds.CircleFeed;
import com.peck.android.fragments.feeds.ExploreFeed;
import com.peck.android.fragments.feeds.HomeFeed;
import com.peck.android.fragments.feeds.PeckFeed;
import com.peck.android.listeners.FragmentSwitcherListener;
import com.peck.android.managers.GcmRegistrar;
import com.peck.android.managers.LoginManager;
import com.squareup.picasso.Picasso;

import java.util.Deque;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingDeque;


/**
 *
 * The main activity for the app.
 *
 */
public class FeedActivity extends FragmentActivity {

    public final static String SELECTION = "sel";
    public final static String TAB_PECKS = "tb_pecks";
    public final static String TAB_EXPLORE = "tb_explore";
    public final static String TAB_POST = "tb_post";
    public final static String TAB_CIRCLES = "tb_circles";
    public final static String TAB_PROFILE = "tb_profile";

    private Deque<HomeFeed> fragQueue = new LinkedBlockingDeque<HomeFeed>(5);

    //hashmap of button resource ids to feeds
    private final static HashMap<Integer, Fragment> buttons = new HashMap<Integer, Fragment>(); //don't use a sparsearray, we need the keyset

    @Nullable
    //we keep the last pressed button so we know whether to cancel or open
    private Button lastPressed;

    {
        buttons.put(R.id.bt_add, new NewPostTab());
        buttons.put(R.id.bt_profile, new ProfileTab());
        buttons.put(R.id.bt_explore, new ExploreFeed());
        buttons.put(R.id.bt_circles, new CircleFeed());
        buttons.put(R.id.bt_peck, new PeckFeed());

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_root);

        //add red/yellow/green indicators to picasso for debugging (red is loaded over network, yellow is loaded from disk, green is loaded from memory)
        if (BuildConfig.DEBUG) Picasso.with(this).setIndicatorsEnabled(true);

        //associate feeds with buttons
        for (final int i : buttons.keySet()) {
            final String tag = "btn " + i;
            FragmentSwitcherListener fragmentSwitcherListener = new FragmentSwitcherListener(getSupportFragmentManager(), buttons.get(i), tag, R.id.ll_feed_content){
                @Override
                public void onClick(View view) {
                    super.onClick(view);

                    getSupportFragmentManager().executePendingTransactions();
                    if (lastPressed != null && lastPressed.equals(view)) toggleVisibility();
                    else findViewById(R.id.ll_feed_content).setVisibility(View.VISIBLE);
                    lastPressed = (Button)view;
                }
            };
            fragmentSwitcherListener.setAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            findViewById(i).setOnClickListener(fragmentSwitcherListener);
        }

        fragQueue.add(new HomeFeed());
        fragQueue.add(new HomeFeed());
        fragQueue.add(new HomeFeed());
        fragQueue.add(new HomeFeed());
        fragQueue.add(new HomeFeed());

        getSupportFragmentManager().beginTransaction().add(R.id.ll_temp, new HomeFeed()).commit();


        /*final ViewPager pager = ((ViewPager) findViewById(R.id.vp_home_feed));
        pager.setOffscreenPageLimit(2);
        final FragmentStatePagerAdapter pagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                HomeFeed ret = fragQueue.removeFirst();
                ret.withRelativeDate(i);
                return ret;
            }

            @Override
            public int getCount() {
                return 5;
            }
        };
        pager.setAdapter(pagerAdapter);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int currentPage;
            @Override
            public void onPageScrolled(int i, float v, int i2) {
                currentPage = i;
            }

            @Override
            public void onPageSelected(int i) {
                switch (((int) Math.signum(i - currentPage))) {
                    case 1:
                        break;
                    case -1:
                        break;
                    case 0:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {}
        });*/



    }

    @Override
    protected void onResume() {
        super.onResume();

        //make sure google play services are enabled
        GcmRegistrar.checkPlayServices(this);

        //check the active account and make sure it's valid
        Account account = LoginManager.getActive();
        if (account == null || !LoginManager.isValid(account)) {
            //if it's not, open a locale selection activity, which will create a new account
            Intent intent = new Intent(FeedActivity.this, LocaleActivity.class);
            startActivity(intent);
            finish();
        } else {
            ContentResolver.addPeriodicSync(account, PeckApp.AUTHORITY, new Bundle(), PeckApp.Constants.Network.POLL_FREQUENCY);
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            ContentResolver.requestSync(account, PeckApp.AUTHORITY, bundle);
        }
    }

    @Override
    public void onBackPressed() {
        findViewById(R.id.ll_feed_content).setVisibility(View.GONE);
    }

    private void toggleVisibility() {
        View view = findViewById(R.id.ll_feed_content);
        view.setVisibility((view.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
    }

}
