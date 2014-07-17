package com.peck.android.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.database.DBUtils;
import com.peck.android.fragments.Feed;
import com.peck.android.fragments.tabs.NewPostTab;
import com.peck.android.fragments.tabs.ProfileTab;
import com.peck.android.listeners.FragmentSwitcherListener;
import com.peck.android.models.Circle;
import com.peck.android.models.DBOperable;
import com.peck.android.models.Event;
import com.peck.android.models.Peck;
import com.peck.android.models.User;

import java.util.HashMap;


public class FeedActivity extends PeckActivity {

    private final static String TAG = "FeedActivity";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private Feed feed2 = new Feed();

    private final static HashMap<Integer, Fragment> buttons = new HashMap<Integer, Fragment>(); //don't use a sparsearray, we need the keyset

    @Nullable
    private Button lastPressed;

    {
        buttons.put(R.id.bt_add, new NewPostTab());
        buttons.put(R.id.bt_profile, new ProfileTab());

        Feed feed = new Feed.Builder(PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().appendPath(DBUtils.getTableName(Event.class)).build(), R.layout.lvitem_event)
                .withBindings(new String[]{Event.TITLE, Event.TEXT}, new int[]{R.id.tv_title, R.id.tv_text}).build();
        buttons.put(R.id.bt_explore, feed);

        feed2 = new Feed.Builder(PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().appendPath(DBUtils.getTableName(Circle.class)).build(), R.layout.lvitem_circle)
                .withBindings(new String[] { Circle.NAME, Circle.MEMBERS }, new int[] { R.id.tv_title, R.id.ll_userfeed })
                .withViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int i) {
                        switch (view.getId()) {
                            case R.id.ll_userfeed:
                                Feed userFeed = new Feed.Builder(PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().appendPath(DBUtils.getTableName(User.class)).build(), R.layout.hlvitem_user)
                                        .withProjection(new String[]{DBOperable.LOCAL_ID, User.FIRST_NAME, User.LAST_NAME})
                                        .withBindings(new String[]{User.FIRST_NAME}, new int[]{R.id.tv_title})
                                        .layout(R.layout.user_feed)
                                        .withViewBinder(new SimpleCursorAdapter.ViewBinder() {
                                            @Override
                                            public boolean setViewValue(View view, Cursor cursor, int i) {
                                                return false;
                                            }
                                        })
                                        .build();
                                feed2.getChildFragmentManager().beginTransaction().add(R.id.ll_userfeed, userFeed).commit();
                                return true;
                            default:
                                return false;
                        }
                    }
                })
                .build();
        buttons.put(R.id.bt_circles, feed2);

        feed = new Feed.Builder(PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().appendPath(DBUtils.getTableName(Peck.class)).build(), R.layout.lvitem_peck)
                .withBindings(new String[]{Peck.NAME, Peck.TEXT}, new int[]{R.id.tv_title, R.id.tv_text}).build();
        buttons.put(R.id.bt_peck, feed);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_root);
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

        Feed feed = new Feed.Builder(PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().appendPath(DBUtils.getTableName(Event.class)).build(), R.layout.lvitem_event)
                .withBindings(new String[]{Event.TITLE, Event.TEXT}, new int[]{R.id.tv_title, R.id.tv_text}).build();
        getSupportFragmentManager().beginTransaction().add(R.id.ll_home_feed, feed).commit();

        if (!checkPlayServices()) {
            //todo: prompt for valid play services download
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();

        if (getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, MODE_PRIVATE).getLong(PeckApp.Constants.Preferences.LOCALE_ID, 0) == 0) {
            Intent intent = new Intent(FeedActivity.this, LocaleActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
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
