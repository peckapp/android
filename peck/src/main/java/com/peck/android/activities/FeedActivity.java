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
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
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

import java.util.HashMap;


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

    HomeFeed[] feeds = new HomeFeed[] {new HomeFeed(), new HomeFeed(), new HomeFeed()};

    private int selectedPage;

    //hashmap of button resource ids to feeds
    private final static HashMap<Integer, Fragment> buttons = new HashMap<Integer, Fragment>(); //don't use a sparsearray, we need the keyset

    @Nullable
    //we keep the last pressed button so we know whether to cancel or open
    private Button lastPressed;

    {
        feeds[0].decrementDate();
        feeds[2].incrementDate();
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

        //getSupportFragmentManager().beginTransaction().add(R.id.ll_temp, new HomeFeed()).commit();

        final ViewPager pager = ((ViewPager) findViewById(R.id.vp_home_feed));

        final FragmentStatePagerAdapter pagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return feeds[i];
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }
        };
        pager.setAdapter(pagerAdapter);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                selectedPage = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (i == ViewPager.SCROLL_STATE_IDLE) {
                    final HomeFeed left = feeds[0];
                    final HomeFeed center = feeds[1];
                    final HomeFeed right = feeds[2];

                    if (selectedPage == 0) {
                        left.decrementDate();
                        center.decrementDate();
                        right.decrementDate();
                        center.resetScroll();
                        right.resetScroll();
                        pager.setCurrentItem(1, false);
                        left.resetScroll();
                    } else if (selectedPage == 2) {
                        left.incrementDate();
                        center.incrementDate();
                        right.incrementDate();
                        center.resetScroll();
                        left.resetScroll();
                        pager.setCurrentItem(1, false);
                        right.resetScroll();
                    }
                }
            }
        });
        pager.setCurrentItem(1, false);
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
