package com.peck.android.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.makeramen.RoundedImageView;
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
import com.peck.android.network.PeckAccountAuthenticator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.sephiroth.android.library.widget.HListView;

public class FeedActivity extends PeckActivity {

    private final static String TAG = "FeedActivity";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    private final static HashMap<Integer, Fragment> buttons = new HashMap<Integer, Fragment>(); //don't use a sparsearray, we need the keyset

    @Nullable
    private Button lastPressed;

    {
        buttons.put(R.id.bt_add, new NewPostTab());
        buttons.put(R.id.bt_profile, new ProfileTab());

        Feed feed = new Feed.Builder(PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().appendPath(DBUtils.getTableName(Event.class)).build(), R.layout.lvitem_explore)
                .withBindings(new String[]{Event.TITLE, Event.IMAGE_URL}, new int[]{R.id.tv_title, R.id.iv_event})
                .withViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int i) {
                        switch (view.getId()) {
                            case R.id.iv_event:
                                String url = cursor.getString(cursor.getColumnIndex(Event.IMAGE_URL));
                                Log.v(FeedActivity.class.getSimpleName(), "url: " + ((url != null) ? url : "null"));
                                /*if (Build.VERSION.SDK_INT >= 17) {
                                    RenderScript renderScript = RenderScript.create(FeedActivity.this);
                                    final Allocation input = Allocation.createFromBitmap(renderScript, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
                                    final Allocation output = Allocation.createTyped(renderScript, input.getType());
                                    final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
                                    script.setRadius(10.f);
                                    script.setInput(input);
                                    script.forEach(output);
                                    Bitmap ret = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
                                    output.copyTo(ret);
                                    ((ImageView) view).setImageBitmap(ret);
                                }*/
                                if (url != null && url.length() > 0) {
                                    Picasso.with(FeedActivity.this)
                                            .load(PeckApp.Constants.Network.BASE_URL + cursor.getString(cursor.getColumnIndex(Event.IMAGE_URL)))
                                            .placeholder(R.drawable.ic_peck)
                                            .into(((ImageView) view));
                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                }).build();
        buttons.put(R.id.bt_explore, feed);

        feed = new Feed.Builder(PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().appendPath(DBUtils.getTableName(Circle.class)).build(), R.layout.lvitem_circle)
                .withBindings(new String[]{Circle.NAME, Circle.MEMBERS}, new int[]{R.id.tv_title, R.id.hlv_users})
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                    }
                })
                .withViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    final SparseArray<ArrayList<Map<String, Object>>> circleMembers = new SparseArray<ArrayList<Map<String, Object>>>();

                    @Override
                    public boolean setViewValue(final View view, final Cursor cursor, int i) {
                        switch (view.getId()) {
                            case R.id.hlv_users:
                                final int circle_id = cursor.getInt(cursor.getColumnIndex(DBOperable.LOCAL_ID));
                                new AsyncTask<Void, Void, ArrayList<Map<String, Object>>>() {
                                    @Override
                                    protected ArrayList<Map<String, Object>> doInBackground(Void... voids) {
                                        ArrayList<Map<String, Object>> ret = circleMembers.get(circle_id);

                                        if (ret == null) {
                                            Cursor nested = getContentResolver().query(PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().appendPath("circles").appendPath(
                                                    Integer.toString(circle_id)).appendPath("users").build(), new String[]{User.FIRST_NAME, User.IMAGE_NAME, User.LOCAL_ID,
                                                    "lower(" + User.FIRST_NAME + ") as lwr_index"}, null, null, "lwr_index");
                                            ret = new ArrayList<Map<String, Object>>();

                                            while (nested.moveToNext()) {
                                                Map<String, Object> map = new HashMap<String, Object>();
                                                map.put(User.FIRST_NAME, nested.getString(nested.getColumnIndex(User.FIRST_NAME)));
                                                map.put(User.IMAGE_NAME, nested.getString(nested.getColumnIndex(User.IMAGE_NAME)));
                                                ret.add(map);
                                            }
                                            circleMembers.put(circle_id, ret);
                                            nested.close();
                                        }

                                        return circleMembers.get(circle_id);
                                    }

                                    @Override
                                    protected void onPostExecute(
                                            final ArrayList<Map<String, Object>> list) {
                                        if (list != null) {
                                            final SimpleAdapter simpleAdapter = new SimpleAdapter(FeedActivity.this, list, R.layout.hlvitem_user,
                                                    new String[]{User.FIRST_NAME, User.IMAGE_NAME}, new int[]{R.id.tv_title, R.id.iv_event});
                                            ((HListView) view).setAdapter(simpleAdapter);
                                            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                                                @Override
                                                public boolean setViewValue(View view, Object o, String s) {
                                                    switch (view.getId()) {
                                                        case R.id.iv_event:
                                                            Log.v(FeedActivity.class.getSimpleName(), "object: " + ((o != null) ? o.toString() : "null"));
                                                            if (o != null && o.toString().length() > 0) {
                                                                Picasso.with(FeedActivity.this)
                                                                        .load(PeckApp.Constants.Network.BASE_URL + o)
                                                                        .placeholder(R.drawable.ic_peck)
                                                                        .into((RoundedImageView) view);
                                                            }
                                                            return true;
                                                    }

                                                    return false;
                                                }
                                            });
                                        }
                                    }
                                }.execute();

                                return true;
                            default:
                                return false;
                        }
                    }
                }).build();

        buttons.put(R.id.bt_circles,feed);

        feed=new Feed.Builder(PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().
                appendPath(DBUtils.getTableName(Peck.class)).build(), R.layout.lvitem_peck)
                .withBindings(new String[]{Peck.NAME, Peck.TEXT}, new int[]{R.id.tv_title, R.id.tv_text})
                .withViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int i) {
                        switch (view.getId()) {
                        }
                        return false;
                    }
                }).
                        build();
        buttons.put(R.id.bt_peck,feed);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_root);

        Picasso.with(this).setLoggingEnabled(true);
        Picasso.with(this).setIndicatorsEnabled(true);

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

        Account account = PeckApp.peekValidAccount();
        if (account == null || AccountManager.get(this).getUserData(account, PeckAccountAuthenticator.INSTITUTION) == null) {
            Intent intent = new Intent(FeedActivity.this, LocaleActivity.class);
            startActivity(intent);
            finish();
        } else {
            ContentResolver.setSyncAutomatically(account, PeckApp.AUTHORITY, true);
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            ContentResolver.requestSync(account, PeckApp.AUTHORITY, bundle);
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
