package com.peck.android.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.NetworkErrorException;
import android.accounts.OperationCanceledException;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.JsonObject;
import com.makeramen.RoundedImageView;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.database.DBUtils;
import com.peck.android.fragments.Feed;
import com.peck.android.fragments.NewPostTab;
import com.peck.android.fragments.ProfileTab;
import com.peck.android.listeners.FragmentSwitcherListener;
import com.peck.android.managers.LoginManager;
import com.peck.android.models.Circle;
import com.peck.android.models.DBOperable;
import com.peck.android.models.Event;
import com.peck.android.models.Peck;
import com.peck.android.models.User;
import com.peck.android.network.JsonUtils;
import com.peck.android.network.PeckAccountAuthenticator;
import com.peck.android.network.ServerCommunicator;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import it.sephiroth.android.library.widget.HListView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FeedActivity extends PeckActivity {

    private final static String TAG = "FeedActivity";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private TimeZone tz = Calendar.getInstance().getTimeZone();

    private final static HashMap<Integer, Fragment> buttons = new HashMap<Integer, Fragment>(); //don't use a sparsearray, we need the keyset

    @Nullable
    private Button lastPressed;

    {
        buttons.put(R.id.bt_add, new NewPostTab());
        buttons.put(R.id.bt_profile, new ProfileTab());

        Feed feed = new Feed.Builder(PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().appendPath(DBUtils.getTableName(Event.class)).build(), R.layout.lvitem_explore)
                .withBindings(new String[]{Event.TITLE, Event.IMAGE_URL}, new int[]{R.id.tv_title, R.id.iv_event})
                .withProjection(new String[]{ Event.TITLE, Event.TYPE, Event.IMAGE_URL, DBOperable.LOCAL_ID })
                .withSelection(Event.TYPE + " = ?", new String[]{Integer.toString(Event.SIMPLE_EVENT)})
                .withViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int i) {
                        switch (view.getId()) {
                            case R.id.iv_event:
                                String url = cursor.getString(cursor.getColumnIndex(Event.IMAGE_URL));
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
                                            .fit()
                                            .centerCrop()
                                            .into(((ImageView) view));
                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                }).build();
        buttons.put(R.id.bt_explore, feed);

        final LayoutInflater inflater = ((LayoutInflater) PeckApp.getContext().getSystemService(LAYOUT_INFLATER_SERVICE));
        View header = inflater.inflate(R.layout.circles_header, null, false);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final View dialogView = inflater.inflate(R.layout.alert_circlecreate, null, false);
                final AlertDialog dialog = new AlertDialog.Builder(FeedActivity.this).setView(dialogView).setPositiveButton("Create", null).setCancelable(true).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View inner) {
                                final String circleName = ((EditText) dialogView.findViewById(R.id.et_name)).getText().toString();
                                Account account = LoginManager.getActive();
                                JsonObject jsonBody = new JsonObject();
                                jsonBody.addProperty(Circle.NAME, circleName);
                                jsonBody.addProperty(Circle.LOCALE, AccountManager.get(FeedActivity.this).getUserData(account, PeckAccountAuthenticator.INSTITUTION));
                                jsonBody.addProperty(Circle.USER_ID, AccountManager.get(FeedActivity.this).getUserData(account, PeckAccountAuthenticator.USER_ID));
                                dialogView.findViewById(R.id.pb_network).setVisibility(View.VISIBLE);

                                try {
                                    Map<String, String> auth = JsonUtils.auth(account);
                                    ServerCommunicator.jsonService.post("circles", new ServerCommunicator.TypedJsonBody(JsonUtils.wrapJson("circle", jsonBody)), auth, new Callback<JsonObject>() {
                                        @Override
                                        public void success(JsonObject object, Response response) {
                                            if (object.get("errors").getAsJsonArray().size() > 0) {
                                                Toast.makeText(FeedActivity.this, "failure: " + object.get("errors").toString(), Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                Toast.makeText(FeedActivity.this, "success", Toast.LENGTH_LONG).show();
                                                dialogView.findViewById(R.id.pb_network).setVisibility(View.GONE);
                                                dialog.dismiss();
                                            }
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            Toast.makeText(FeedActivity.this, "Network error posting circle: " + error.getResponse().getStatus() + " - " + error.getResponse().getReason(), Toast.LENGTH_LONG).show();
                                            dialogView.findViewById(R.id.pb_network).setVisibility(View.GONE);
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (OperationCanceledException e) {
                                    e.printStackTrace();
                                } catch (AuthenticatorException e) {
                                    e.printStackTrace();
                                } catch (LoginManager.InvalidAccountException e) {
                                    e.printStackTrace();
                                } catch (NetworkErrorException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                });
                dialog.show();

            }
        });

        feed = new Feed.Builder(PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().appendPath(DBUtils.getTableName(Circle.class)).build(), R.layout.lvitem_circle)
                .withBindings(new String[]{Circle.NAME, Circle.MEMBERS, Circle.NAME}, new int[]{R.id.tv_title, R.id.hlv_users, R.id.tv_add})
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                })
                .withHeader(header)
                .orderedBy(DBOperable.UPDATED_AT + " desc")
                .withViewBinder(new SimpleCursorAdapter.ViewBinder() {

                    final SparseArray<ArrayList<Map<String, Object>>> circleMembers = new SparseArray<ArrayList<Map<String, Object>>>();

                    @Override
                    public boolean setViewValue(final View view, final Cursor cursor, int i) {
                        switch (view.getId()) {
                            case R.id.hlv_users:
                                final int circle_id = cursor.getInt(cursor.getColumnIndex(DBOperable.LOCAL_ID));
                                new AsyncTask<Void, Void, Void>() {
                                    ArrayList<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
                                    SimpleAdapter simpleAdapter;

                                    @Override
                                    protected void onPreExecute() {
                                        synchronized (circleMembers) {
                                            ArrayList<Map<String, Object>> objs = circleMembers.get(circle_id);
                                            if (objs != null) ret.addAll(objs);
                                        }

                                        simpleAdapter = new SimpleAdapter(FeedActivity.this, ret, R.layout.hlvitem_user,
                                                new String[]{User.FIRST_NAME, User.IMAGE_NAME}, new int[]{R.id.tv_title, R.id.iv_event});

                                        ((HListView) view).setAdapter(simpleAdapter);
                                        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                                            @Override
                                            public boolean setViewValue(View view, Object o, String s) {
                                                switch (view.getId()) {
                                                    case R.id.iv_event:
                                                        if (o != null && o.toString().length() > 0) {
                                                            Picasso.with(FeedActivity.this)
                                                                    .load(PeckApp.Constants.Network.BASE_URL + o)
                                                                    .fit()
                                                                    .centerCrop()
                                                                    .into((RoundedImageView) view);
                                                        }
                                                        return true;
                                                }

                                                return false;
                                            }
                                        });
                                    }

                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        Cursor second = getContentResolver().query(DBUtils.buildLocalUri(Circle.class).buildUpon().appendPath(Integer.toString(circle_id)).appendPath("users").build(),
                                                new String[] { User.FIRST_NAME, User.IMAGE_NAME, User.LOCAL_ID }, null, null, null);

                                        ret = new ArrayList<Map<String, Object>>();

                                        while (second.moveToNext()) {
                                            Map<String, Object> map = new HashMap<String, Object>();
                                            map.put(User.FIRST_NAME, second.getString(second.getColumnIndex(User.FIRST_NAME)));
                                            map.put(User.IMAGE_NAME, second.getString(second.getColumnIndex(User.IMAGE_NAME)));
                                            ret.add(map);
                                        }
                                        second.close();
                                        synchronized (circleMembers) {
                                            circleMembers.put(circle_id, ret);
                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(final Void avoid) {
                                        simpleAdapter.notifyDataSetChanged();
                                    }
                                }.execute();
                                return true;
                            case R.id.tv_add:
                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.d(FeedActivity.class.getSimpleName(), "clicked");

                                    }
                                });
                                return true;
                            default:
                                return false;
                        }
                    }
                }).build();

        buttons.put(R.id.bt_circles,feed);

        feed = new Feed.Builder(PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().
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
                .withBindings(new String[]{Event.TYPE, Event.TYPE, Event.IMAGE_URL}, new int[]{R.id.tv_title, R.id.tv_text, R.id.iv_event})
                .withProjection(new String[]{Event.TITLE, Event.TEXT, Event.ATHLETIC_OPPONENT, Event.DINING_OP_TYPE, DBOperable.LOCAL_ID, Event.TYPE,
                        Event.DINING_START_TIME, Event.DINING_END_TIME, Event.IMAGE_URL, Event.ANNOUNCEMENT_TITLE, Event.ANNOUNCEMENT_TEXT, Event.UPDATED_AT})
                .orderedBy(Event.UPDATED_AT + " desc")
                .withViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(final View view, final Cursor cursor, int i) {
                        switch (cursor.getInt(i)) {
                            case Event.ATHLETIC_EVENT:
                                switch (view.getId()) {
                                    case R.id.tv_title:
                                        ((TextView) view).setText(cursor.getString(cursor.getColumnIndex(Event.ATHLETIC_OPPONENT)));
                                        return true;
                                    case R.id.tv_text:
                                        ((TextView) view).setText(cursor.getString(cursor.getColumnIndex(Event.ATHLETIC_NOTE)));
                                        return true;
                                    case R.id.iv_event:
                                        Picasso.with(FeedActivity.this)
                                                .load(R.drawable.ic_peck)
                                                .into((ImageView) view);
                                        return true;
                                    default:
                                        return false;
                                }

                            case Event.DINING_OPPORTUNITY:
                                switch (view.getId()) {
                                    case R.id.tv_title:
                                        ((TextView) view).setText(cursor.getString(cursor.getColumnIndex(Event.DINING_OP_TYPE)));
                                        return true;
                                    case R.id.tv_text:
                                        DateTime start = new DateTime(cursor.getLong(cursor.getColumnIndex(Event.DINING_START_TIME))).toDateTime(DateTimeZone.forTimeZone(tz));
                                        DateTime end = new DateTime(cursor.getLong(cursor.getColumnIndex(Event.DINING_END_TIME))).toDateTime(DateTimeZone.forTimeZone(tz));
                                        ((TextView) view).setText(start.toString("K:mm") + " - " + end.toString("K:mm"));
                                        return true;
                                    case R.id.iv_event:
                                        Picasso.with(FeedActivity.this)
                                                .load(R.drawable.ic_peck)
                                                .into((ImageView) view);
                                        return true;
                                    default:
                                        return false;
                                }

                            case Event.SIMPLE_EVENT:
                                switch (view.getId()) {
                                    case R.id.tv_title:
                                        ((TextView) view).setText(cursor.getString(cursor.getColumnIndex(Event.TITLE)));
                                        return true;
                                    case R.id.tv_text:
                                        ((TextView) view).setText(cursor.getString(cursor.getColumnIndex(Event.TEXT)));
                                        return true;
                                    case R.id.iv_event:
                                        String urlPath = cursor.getString(cursor.getColumnIndex(Event.IMAGE_URL));
                                        if (urlPath != null && urlPath.length() != 0) {
                                            Picasso.with(FeedActivity.this)
                                                    .load(PeckApp.Constants.Network.BASE_URL + urlPath)
                                                    .fit()
                                                    .centerCrop()
                                                    .into((ImageView) view);
                                        } else {
                                            Picasso.with(FeedActivity.this)
                                                    .load(R.drawable.ic_peck)
                                                    .into((ImageView)view);
                                        }
                                        return true;
                                    default:
                                        return false;
                                }
                            case Event.ANNOUNCEMENT:
                                switch (view.getId()) {
                                    case R.id.tv_title:
                                        ((TextView) view).setText(cursor.getString(cursor.getColumnIndex(Event.ANNOUNCEMENT_TITLE)));
                                        return true;
                                    case R.id.tv_text:
                                        ((TextView) view).setText(cursor.getString(cursor.getColumnIndex(Event.ANNOUNCEMENT_TEXT)));
                                        return true;
                                    default:
                                        return false;
                                }
                        }
                        return false;
                    }
                })
                .build();
        getSupportFragmentManager().beginTransaction().add(R.id.ll_home_feed, feed).commit();

        if (!checkPlayServices()) {
            //todo: prompt for valid play services download
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();

        Account account = LoginManager.getActive();
        if (account == null || !LoginManager.isValid(account)) {
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
