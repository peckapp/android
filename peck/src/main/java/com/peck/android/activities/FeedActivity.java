package com.peck.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;

import com.crashlytics.android.Crashlytics;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.database.helper.LocaleOpenHelper;
import com.peck.android.database.source.DataSource;
import com.peck.android.fragments.tabs.BaseTab;
import com.peck.android.fragments.tabs.CirclesFeed;
import com.peck.android.fragments.tabs.EventFeed;
import com.peck.android.fragments.tabs.NewPostTab;
import com.peck.android.fragments.tabs.NewsFeed;
import com.peck.android.fragments.tabs.PeckFeed;
import com.peck.android.fragments.tabs.ProfileTab;
import com.peck.android.managers.LocaleManager;
import com.peck.android.models.Locale;

import java.util.HashMap;

public class FeedActivity extends PeckActivity implements Animation.AnimationListener {

    private final static String TAG = "FeedActivity";

    private final static HashMap<Integer, BaseTab> hash = new HashMap<Integer, BaseTab>(); //don't use a sparsearray, we need the keyset

    private final static int REVERT = -1000;

    static {
        hash.put(R.id.bt_add, new NewPostTab());
        hash.put(R.id.bt_peck, new PeckFeed());
        hash.put(R.id.bt_profile, new ProfileTab());
        hash.put(R.id.bt_circles, new CirclesFeed());
        hash.put(R.id.bt_newsfeed, new NewsFeed());
        hash.put(R.id.bt_null, new EventFeed());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        setContentView(R.layout.activity_feed_root);

        //test: remove before production
        deleteDatabase(PeckApp.Constants.Database.DATABASE_NAME); //TEST: remove before production
        SharedPreferences.Editor edit = getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, MODE_PRIVATE).edit();
        edit.clear();
        edit.commit();


        for (int i : hash.keySet()) {
            findViewById(i).setOnClickListener(
                    new FragmentSwitcherListener(hash.get(i), "btn " + i, this, R.id.ll_feed_content));
        }

        findViewById(R.id.bt_null).performClick();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_OK: {
                switch (resultCode) {


                }

                break;
            }
            default: {
                break;
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LocaleManager.getManager().getLocale(new DataSource<Locale>(LocaleOpenHelper.getHelper()), this) == null) {
            Intent intent = new Intent(this, LocaleActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        findViewById(R.id.bt_null).performClick();
    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }


    /**
     * Created by mammothbane on 6/18/2014.
     */
    public static class FragmentSwitcherListener implements View.OnClickListener {
        private Fragment f;
        private String tag;
        private FragmentActivity activity;
        private int containerId;

        public FragmentSwitcherListener(Fragment f, String tag, FragmentActivity activity, int containerId) {
            this.f = f;
            this.tag = tag;
            this.activity = activity;
            this.containerId = containerId;
        }

        @Override
        public void onClick(View view) {
            android.support.v4.app.FragmentManager fm = activity.getSupportFragmentManager();
            Fragment tempfrag = fm.findFragmentById(containerId);

            if (tempfrag != f) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                if (tempfrag != null) {
                    ft.detach(tempfrag);
                }
                if (fm.findFragmentByTag(tag) == null) {
                    ft.add(containerId, f, tag);
                } else {
                    ft.attach(f);
                }
                ft.commit();
            }
        }
    }
}
