package com.peck.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.peck.android.R;
import com.peck.android.fragments.BaseTab;
import com.peck.android.fragments.tabs.CirclesFeed;
import com.peck.android.fragments.tabs.ExploreFeed;
import com.peck.android.fragments.tabs.NewPostTab;
import com.peck.android.fragments.tabs.PeckFeed;
import com.peck.android.fragments.tabs.ProfileTab;
import com.peck.android.listeners.FragmentSwitcherListener;
import com.peck.android.managers.LocaleManager;

import java.util.HashMap;

public class FeedActivity extends PeckActivity {

    private final static String TAG = "FeedActivity";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private final static HashMap<Integer, BaseTab> buttons = new HashMap<Integer, BaseTab>(); //don't use a sparsearray, we need the keyset

    static {
        buttons.put(R.id.bt_add, new NewPostTab());
        buttons.put(R.id.bt_peck, new PeckFeed());
        buttons.put(R.id.bt_profile, new ProfileTab());
        buttons.put(R.id.bt_circles, new CirclesFeed());
        buttons.put(R.id.bt_newsfeed, new ExploreFeed());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        setContentView(R.layout.activity_feed_root);

        for (final int i : buttons.keySet()) {
            final String tag = "btn " + i;
            FragmentSwitcherListener fragmentSwitcherListener = new FragmentSwitcherListener(getSupportFragmentManager(), buttons.get(i), tag, R.id.ll_feed_content){
                @Override
                public void onClick(View view) {
                    Fragment temp = getSupportFragmentManager().findFragmentById(R.id.ll_feed_content);
                    if (temp != null && temp.equals(buttons.get(i))) {
                        detachCurrentFragment();
                    } else {
                        super.onClick(view);
                    }

                }
            };
            fragmentSwitcherListener.setAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            findViewById(i).setOnClickListener(fragmentSwitcherListener);
        }

        if (!checkPlayServices()) {
            //todo: prompt for valid play services download
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();


        if (LocaleManager.getManager().getLocale() == null) {
            Intent intent = new Intent(FeedActivity.this, LocaleActivity.class);
            startActivity(intent);
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
        detachCurrentFragment();
    }

    private void detachCurrentFragment() {
        Fragment temp = getSupportFragmentManager().findFragmentById(R.id.ll_feed_content);
        if (temp != null) {
            getSupportFragmentManager().beginTransaction().detach(temp).commit();
        }
    }




}
