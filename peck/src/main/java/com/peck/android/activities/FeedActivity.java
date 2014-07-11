package com.peck.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.makeramen.RoundedImageView;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.adapters.ViewAdapter;
import com.peck.android.fragments.Feed;
import com.peck.android.fragments.tabs.NewPostTab;
import com.peck.android.fragments.tabs.ProfileTab;
import com.peck.android.listeners.FragmentSwitcherListener;
import com.peck.android.managers.LocaleManager;
import com.peck.android.models.Circle;
import com.peck.android.models.Event;
import com.peck.android.models.Peck;
import com.peck.android.models.User;
import com.peck.android.policies.FiltrationPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class FeedActivity extends PeckActivity {

    private final static String TAG = "FeedActivity";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private final static HashMap<Integer, Fragment> buttons = new HashMap<Integer, Fragment>(); //don't use a sparsearray, we need the keyset

    @Nullable
    private Button lastPressed;

    static {
        buttons.put(R.id.bt_add, new NewPostTab());
        buttons.put(R.id.bt_profile, new ProfileTab());

        Feed feed;

        feed = new Feed<Peck>();
        Bundle bundle = new Bundle();
        bundle.putString(Feed.CLASS_NAME, "com.peck.android.models.Peck");
        bundle.putInt(Feed.FEED_ITEM_LAYOUT, R.layout.lvitem_peck);
        feed.setArguments(bundle);
        feed.setViewAdapter(new ViewAdapter<Peck>() {
            @Override
            public void setUp(View v, Peck item) {
                ((TextView)v.findViewById(R.id.tv_text)).setText(item.getText());
                ((TextView)v.findViewById(R.id.tv_title)).setText(item.getTitle());
            }
        });
        buttons.put(R.id.bt_peck, feed);

        feed = new Feed<Circle>();
        bundle = new Bundle();
        bundle.putString(Feed.CLASS_NAME, "com.peck.android.models.Circle");
        bundle.putInt(Feed.FEED_ITEM_LAYOUT, R.layout.lvitem_circle);
        feed.setArguments(bundle);
        feed.setViewAdapter(new ViewAdapter<Circle>() {
            @Override
            public void setUp(View v, final Circle item) {
                ((TextView)v.findViewById(R.id.tv_title)).setText(item.getTitle());
                Feed<User> userFeed = new Feed<User>();
                Bundle b = new Bundle();
                b.putString(Feed.CLASS_NAME, "com.peck.android.models.User");
                b.putInt(Feed.LAYOUT_RES, R.layout.user_feed);
                b.putInt(Feed.FEED_ITEM_LAYOUT, R.layout.hlvitem_user);
                userFeed.setArguments(b);
                userFeed.setViewAdapter(new ViewAdapter<User>() {
                    @Override
                    public void setUp(final View v, final User item) {
                        final RoundedImageView roundedImageView = (RoundedImageView)v.findViewById(R.id.riv_user);
                        roundedImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("User " + item.getLocalId(), "I was clicked");
                                //todo: open the user's profile page
                            }
                        });

                        //todo: set onLongClickListener

                        if (item.getProfileUrl().length() > 0)
                            Picasso.with(PeckApp.getContext())
                                    .load(item.getProfileUrl())
                                    .into(roundedImageView);

                        if (item.getFullName() != null) ((TextView)v.findViewById(R.id.tv_name)).setText(item.getFullName());
                    }
                });
                userFeed.setFiltrationPolicy(new FiltrationPolicy<User>() {
                    @Override
                    public boolean test(User user) {
                        return (user.getServerId() != null && item.getUserIds().contains(user.getServerId()));
                    }

                    @Override
                    public int compare(User user, User user2) {
                        return user.compareTo(user2);
                    }
                });
            }
        });
        buttons.put(R.id.bt_circles, feed);

        feed = new Feed<Event>();
        bundle = new Bundle();
        bundle.putString(Feed.CLASS_NAME, "com.peck.android.models.Event");
        bundle.putInt(Feed.FEED_ITEM_LAYOUT, R.layout.lvitem_event);
        feed.setArguments(bundle);
        feed.setViewAdapter(new ViewAdapter<Event>() {
            @Override
            public void setUp(View v, Event item) {
                ((TextView)v.findViewById(R.id.tv_title)).setText(item.getTitle());
                ((TextView)v.findViewById(R.id.tv_text)).setText(item.getText());
            }
        });
        buttons.put(R.id.bt_explore, feed);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK: {
                break;
            }
            default: {
                break;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        setContentView(R.layout.activity_feed_root);

        Feed<Event> homeFeed = new Feed<Event>();
        Bundle bundle = new Bundle();
        bundle.putString(Feed.CLASS_NAME, "com.peck.android.models.Event");
        bundle.putInt(Feed.FEED_ITEM_LAYOUT, R.layout.lvitem_event);
        homeFeed.setArguments(bundle);
        homeFeed.setViewAdapter(new ViewAdapter<Event>() {
            @Override
            public void setUp(View v, Event item) {
                ((TextView)v.findViewById(R.id.tv_title)).setText(item.getTitle());
                ((TextView)v.findViewById(R.id.tv_text)).setText(item.getText());
            }
        });

        getSupportFragmentManager().beginTransaction().add(R.id.ll_home_feed, homeFeed).commit();

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

        if (!checkPlayServices()) {
            //todo: prompt for valid play services download
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();

        if (LocaleManager.getLocale() == null) {
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
        findViewById(R.id.ll_feed_content).setVisibility(View.GONE);
    }

    private void toggleVisibility() {
        View view = findViewById(R.id.ll_feed_content);
        view.setVisibility((view.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
    }




}
