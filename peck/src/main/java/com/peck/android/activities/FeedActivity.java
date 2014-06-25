package com.peck.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;

import com.crashlytics.android.Crashlytics;
import com.peck.android.R;
import com.peck.android.fragments.BaseTab;
import com.peck.android.fragments.tabs.CirclesFeed;
import com.peck.android.fragments.tabs.ExploreFeed;
import com.peck.android.fragments.tabs.NewPostTab;
import com.peck.android.fragments.tabs.PeckFeed;
import com.peck.android.fragments.tabs.ProfileTab;
import com.peck.android.interfaces.Callback;
import com.peck.android.listeners.FragmentSwitcherListener;
import com.peck.android.managers.LocaleManager;
import com.peck.android.models.Locale;

import java.util.HashMap;

public class FeedActivity extends PeckActivity implements Animation.AnimationListener {

    private final static String TAG = "FeedActivity";

    private final static HashMap<Integer, BaseTab> buttons = new HashMap<Integer, BaseTab>(); //don't use a sparsearray, we need the keyset

    private FragmentSwitcherListener.Selector selector = new FragmentSwitcherListener.Selector();

    private Fragment selected = null;

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
            findViewById(i).setOnClickListener(new FragmentSwitcherListener(getSupportFragmentManager(), buttons.get(i), "btn " + i, R.id.ll_feed_content, selector){
                @Override
                public void onClick(View view) {
                    if (!(selector.getSelected() == null) && selector.getSelected().equals(buttons.get(i))) onBackPressed();
                    else {
                        findViewById(R.id.frag_home).setVisibility(View.GONE);
                        super.onClick(view);
                    }
                }
            } );
        }
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

        LocaleManager.getManager().getLocale(new Callback<Locale>() {
            @Override
            public void callBack(Locale obj) {
                if (obj == null) {
                    Intent intent = new Intent(FeedActivity.this, LocaleActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        selected = null;
        findViewById(R.id.frag_home).setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(getSupportFragmentManager().findFragmentById(R.id.ll_feed_content));
        fragmentTransaction.commit();
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
}
