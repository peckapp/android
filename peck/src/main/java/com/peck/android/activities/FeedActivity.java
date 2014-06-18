package com.peck.android.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.facebook.Session;
import com.facebook.SessionState;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.database.source.LocaleDataSource;
import com.peck.android.fragments.SimpleFragment;
import com.peck.android.fragments.tabs.BaseTab;
import com.peck.android.fragments.tabs.CirclesTab;
import com.peck.android.fragments.DiningFragment;
import com.peck.android.fragments.tabs.EventFeed;
import com.peck.android.fragments.tabs.NewPostTab;
import com.peck.android.fragments.tabs.PeckFeed;
import com.peck.android.fragments.tabs.ProfileTab;
import com.peck.android.fragments.tabs.NewsFeed;
import com.peck.android.interfaces.HasTabTag;
import com.peck.android.managers.LocaleManager;
import com.peck.android.models.Event;
import com.peck.android.models.Profile;

import java.util.HashMap;

public class FeedActivity extends PeckActivity implements Animation.AnimationListener {

    private final static String TAG = "FeedActivity";

    private final static HashMap<Integer, Class<? extends BaseTab>> hash = new HashMap<Integer, Class<? extends BaseTab>>();

    private final static int REVERT = -1000;

    static {
        hash.put(R.id.bt_add, NewPostTab.class);
        hash.put(R.id.bt_peck, PeckFeed.class);
        hash.put(R.id.bt_profile, ProfileTab.class);
        hash.put(R.id.bt_circles, CirclesTab.class);
        hash.put(R.id.bt_newsfeed, NewsFeed.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_root);

        //test: remove before production
        deleteDatabase(PeckApp.Constants.Database.DATABASE_NAME); //TEST: remove before production
        SharedPreferences.Editor edit = getSharedPreferences(PeckApp.USER_PREFS, MODE_PRIVATE).edit();
        edit.clear();
        edit.commit();


        Bundle b;
        SimpleFragment frag;

        for (int i : hash.keySet()) {
            b = new Bundle();
            b.putInt(SimpleFragment.RESOURCE, hash.get(i));
            frag = new SimpleFragment();
            frag.setArguments(b);
            findViewById(i).setOnClickListener(new newPostListener(frag, "btn " + i));
        }





        for (final Integer i: hash.keySet()) {
            (findViewById(i)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    open(i);
                }
            });
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        EventFeed ef = new EventFeed();
        ft.add(R.id.ll_feed_content, ef, getString(ef.getTabTag()));
        ft.addToBackStack(null);
        ft.commit();

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_OK: {
                switch (resultCode){


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
        if (LocaleManager.getManager().getLocale(new LocaleDataSource(this), this) == null) {
            Intent intent = new Intent(this, LocaleActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        EventFeed ef = new EventFeed();
        addAttachIfExists(getString(ef.getTabTag()), ef);
    }

    private void addAttachIfExists(String fragId, Fragment f) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment frag = null;
        Fragment repFrag = null;

        try { frag = getSupportFragmentManager().findFragmentByTag(fragId); } catch (NullPointerException e) {}
        try { repFrag = getSupportFragmentManager().findFragmentById(R.id.ll_feed_content); } catch (NullPointerException e) {}

        try {
            if (frag == null) {
                //Log.d(TAG, null);
                ft.add(R.id.ll_feed_content, f);
            } else {
                ft.attach(frag);
            }
        } catch (Exception e) { e.printStackTrace(); }

        if (repFrag != null) {
            ft.detach(repFrag);
        }

        //ft.addToBackStack(null);

        ft.commit();

    }


    private void open(int id) {
        Class<? extends BaseTab> clss = hash.get(id);
        String fragId = null;

        try { fragId = getString(hash.get(id).newInstance().getTabTag());
            addAttachIfExists(fragId, (Fragment)hash.get(id).newInstance());
        } catch (Exception e) {

            e.printStackTrace();
        }


    }

    @Override
    public void onAnimationEnd(Animation animation){}

    @Override
    public void onAnimationRepeat(Animation animation){}

    @Override
    public void onAnimationStart(Animation animation) {

    }


}
