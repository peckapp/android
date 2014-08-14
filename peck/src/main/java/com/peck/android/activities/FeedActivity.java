/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

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
import android.database.MatrixCursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.makeramen.RoundedImageView;
import com.peck.android.BuildConfig;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.database.DBUtils;
import com.peck.android.fragments.Feed;
import com.peck.android.fragments.NewPostTab;
import com.peck.android.fragments.ProfileTab;
import com.peck.android.listeners.FragmentSwitcherListener;
import com.peck.android.managers.GcmRegistrar;
import com.peck.android.managers.LoginManager;
import com.peck.android.models.Circle;
import com.peck.android.models.DBOperable;
import com.peck.android.models.Event;
import com.peck.android.models.Peck;
import com.peck.android.models.User;
import com.peck.android.models.joins.CircleMember;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import it.sephiroth.android.library.widget.HListView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


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

    private TimeZone tz = Calendar.getInstance().getTimeZone();

    //traveling search view
    private AutoCompleteTextView tView;

    //we use this to get access to the circles feed in its own builder
    private Feed tFeed = null;

    //the circlefeed's current add position
    private int currentCircleAddPos = -1;

    //the current circle
    private long currentCircleId = -1;

    //hashmap of button resource ids to feeds
    private final static HashMap<Integer, Fragment> buttons = new HashMap<Integer, Fragment>(); //don't use a sparsearray, we need the keyset

    @Nullable
    //we keep the last pressed button so we know whether to cancel or open
    private Button lastPressed;

    {
        //set up the circle member search view
        tView = new AutoCompleteTextView(PeckApp.getContext()) {


        };

        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 16, 16, 16);
        tView.setLayoutParams(params);
        tView.setTextColor(PeckApp.getContext().getResources().getColor(android.R.color.primary_text_light_nodisable));
        tView.setBackgroundColor(PeckApp.getContext().getResources().getColor(R.color.white));
        tView.setDropDownBackgroundDrawable(new ColorDrawable(R.color.white));
        tView.setDropDownVerticalOffset(8);
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(PeckApp.getContext(), R.layout.frag_search, null, new String[] { User.FIRST_NAME }, new int[] {R.id.tv_title}, 0);
        tView.setAdapter(adapter);
        //return the first 10 results
        FilterQueryProvider queryProvider = new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence charSequence) {
                Cursor src = adapter.getCursor();
                if (src == null || src.getCount() == 0 || charSequence == null || charSequence.length() == 0) return src;
                MatrixCursor cursor = new MatrixCursor(new String[]{User.SV_ID, User.FIRST_NAME});
                String regex = "";
                for (char cha : charSequence.toString().toCharArray()) {
                    regex += cha;
                    regex += "[a-zA-Z]*";
                }
                Pattern pattern = Pattern.compile(regex);
                while (src.moveToNext() && cursor.getCount() < 10) {
                    if (pattern.matcher(src.getString(src.getColumnIndex(User.FIRST_NAME))).matches()) {
                        cursor.addRow(new Object[]{src.getLong(src.getColumnIndex(User.SV_ID)), src.getString(src.getColumnIndex(User.FIRST_NAME))});
                    }
                }
                return src;
            }
        };

        adapter.setFilterQueryProvider(queryProvider);

        tView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, final long l) {
                tView.setText("");

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        JsonObject object = new JsonObject();
                        object.addProperty(CircleMember.LOCALE, Integer.parseInt(AccountManager.get(FeedActivity.this).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.INSTITUTION)));
                        object.addProperty(CircleMember.CIRCLE_ID, currentCircleId);
                        object.addProperty(CircleMember.USER_ID, l);
                        object.addProperty(CircleMember.INVITED_BY, AccountManager.get(FeedActivity.this).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.USER_ID));
                        if (currentCircleAddPos < 0)
                            throw new IllegalArgumentException("must set currentcircle before calling this method");
                        try {
                            ServerCommunicator.jsonService.post("circle_members", new ServerCommunicator.TypedJsonBody(JsonUtils.wrapJson("circle_member", object)), JsonUtils.auth(LoginManager.getActive()), new Callback<JsonObject>() {
                                @Override
                                public void success(JsonObject jsonObject, Response response) {
                                    JsonArray errors = jsonObject.getAsJsonArray("errors");
                                    if (errors.size() > 0) {
                                        Toast.makeText(FeedActivity.this, errors.toString(), Toast.LENGTH_LONG).show();
                                    } else {
                                        DBUtils.syncJson(DBUtils.buildLocalUri(CircleMember.class), jsonObject.getAsJsonObject("circle_member"), CircleMember.class);
                                        Log.d(FeedActivity.class.getSimpleName(), "success adding user " + jsonObject.getAsJsonObject("circle_member").get(CircleMember.USER_ID).getAsLong());
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Log.e(FeedActivity.class.getSimpleName(), "failure adding circle member");
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

                        return null;
                    }
                }.execute();
            }
        });

        //add the simple tabs
        buttons.put(R.id.bt_add, new NewPostTab());
        buttons.put(R.id.bt_profile, new ProfileTab());

        //add the explore feed
        Feed feed = new Feed.Builder(PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().appendPath(DBUtils.getTableName(Event.class)).build(), R.layout.lvitem_explore)
                .withBindings(
                        new String[]{Event.TITLE, Event.IMAGE_URL, Event.IMAGE_URL, Event.IMAGE_URL, Event.USER_ID, Event.TEXT, Event.START_DATE, Event.USER_ID, Event.USER_ID, Event.USER_ID },
                        new int[]{R.id.tv_title, R.id.iv_event, R.id.rl_image, R.id.riv_user, R.id.tv_text, R.id.tv_time, R.id.tv_name, R.id.tv_action, R.id.rl_photo})
                .withProjection(new String[]{Event.TITLE, Event.TYPE, Event.IMAGE_URL, DBOperable.LOCAL_ID, Event.USER_ID, Event.TEXT, Event.START_DATE, Event.SR_ID,
                        Event.ANNOUNCEMENT_TEXT, Event.ANNOUNCEMENT_COMMENT_COUNT,
                        Event.ATHLETIC_OPPONENT, Event.ATHLETIC_DATE_AND_TIME, Event.ATHLETIC_NOTE, Event.ATHLETIC_HOME_SCORE, Event.ATHLETIC_HOME_AWAY, Event.ATHLETIC_AWAY_SCORE, Event.ATHLETIC_LOCATION})
                .withSelection(Event.TYPE + " = ? OR " + Event.TYPE + " = ? OR " + Event.TYPE + " = ?", new String[]{Integer.toString(Event.SIMPLE_EVENT), Integer.toString(Event.ATHLETIC_EVENT), Integer.toString(Event.ANNOUNCEMENT)})
                .orderedBy(Event.UPDATED_AT + " desc")
                .withRecycleRunnable(new Feed.RecycleRunnable() {
                    @Override
                    public void run() {

                    }
                })
                .withViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(final View view, final Cursor cursor, int i) {
                        final long user_id = cursor.getLong(cursor.getColumnIndex(Event.USER_ID));
                        int type = cursor.getInt(cursor.getColumnIndex(Event.TYPE));
                        switch (type) {
                            case Event.SIMPLE_EVENT:
                                switch (view.getId()) {
                                    case R.id.iv_event:
                                        String url = cursor.getString(cursor.getColumnIndex(Event.IMAGE_URL));
                                        if (url != null && !url.isEmpty()) {
                                            Picasso.with(FeedActivity.this)
                                                    .load(PeckApp.Constants.Network.BASE_URL + cursor.getString(cursor.getColumnIndex(Event.IMAGE_URL)))
                                                    .fit()
                                                    .centerCrop()
                                                    .into(((ImageView) view));
                                        }
                                        return true;
                                    case R.id.rl_image:
                                        String nUrl = cursor.getString(cursor.getColumnIndex(Event.IMAGE_URL));
                                        if (nUrl == null || nUrl.isEmpty() || nUrl.equals("/images/missing.png")) {
                                            view.setVisibility(View.GONE);
                                        } else {
                                            view.setVisibility(View.VISIBLE);
                                        }
                                        return true;
                                    case R.id.riv_user:
                                        if (user_id > 0)
                                            new AsyncTask<Void, Void, String>() {
                                                @Override
                                                protected String doInBackground(Void... voids) {
                                                    Cursor c = getContentResolver().query(DBUtils.buildLocalUri(User.class), new String[]{User.LOCAL_ID, User.SV_ID, User.THUMBNAIL, User.IMAGE_NAME},
                                                            User.SV_ID + " = ?", new String[]{Long.toString(user_id)}, null);
                                                    if (c.getCount() == 0) return null;
                                                    c.moveToFirst();
                                                    String ret = c.getString(c.getColumnIndex(User.IMAGE_NAME));
                                                    c.close();
                                                    return ret;
                                                }

                                                @Override
                                                protected void onPostExecute(String s) {
                                                    if (s != null && !s.isEmpty()) {
                                                        Picasso.with(FeedActivity.this)
                                                                .load(PeckApp.Constants.Network.BASE_URL + s)
                                                                .fit()
                                                                .centerCrop()
                                                                .into(((ImageView) view));
                                                    }
                                                }
                                            }.execute();
                                        return true;
                                    case R.id.tv_text:
                                        String s = cursor.getString(cursor.getColumnIndex(Event.TEXT));
                                        ((TextView) view).setText(s);
                                        return true;
                                    case R.id.tv_title:
                                        String s2 = cursor.getString(cursor.getColumnIndex(Event.TITLE));
                                        ((TextView) view).setText(s2);
                                        return true;
                                    case R.id.tv_time:
                                        long l = cursor.getLong(cursor.getColumnIndex(Event.START_DATE))*1000l;
                                        if (l > 0)
                                            ((TextView) view).setText(new DateTime(l).toDateTime(DateTimeZone.forTimeZone(tz)).toString("MMM d"));
                                        else ((TextView) view).setText("");
                                        return true;
                                    case R.id.tv_name:
                                        new AsyncTask<Void, Void, String>() {
                                            @Override
                                            protected String doInBackground(Void... voids) {
                                                if (user_id < 1) return "Someone";
                                                Cursor c = getContentResolver().query(DBUtils.buildLocalUri(User.class), new String[]{User.LOCAL_ID, User.SV_ID, User.FIRST_NAME, User.LAST_NAME},
                                                        User.SV_ID + " = ?", new String[]{Long.toString(user_id)}, null);
                                                if (c.getCount() == 0) return "Someone";
                                                c.moveToFirst();
                                                String ret = c.getString(c.getColumnIndex(User.FIRST_NAME)) + " " + c.getString(c.getColumnIndex(User.LAST_NAME));
                                                c.close();
                                                return ret;
                                            }

                                            @Override
                                            protected void onPostExecute(String s) {
                                                ((TextView) view).setText(s);
                                            }
                                        }.execute();
                                        return true;
                                    case R.id.tv_action:
                                        if (user_id > 0 || cursor.getInt(cursor.getColumnIndex(Event.SR_ID)) < 1)
                                            ((TextView) view).setText("posted an event");
                                        else ((TextView) view).setText("");
                                        return true;
                                    case R.id.rl_photo:
                                        if (user_id > 0) {
                                            view.setVisibility(View.VISIBLE);
                                        } else {
                                            view.setVisibility(View.GONE);
                                        }
                                        return true;
                                    default:
                                        return false;
                                }
                            case Event.ANNOUNCEMENT:
                                switch (view.getId()) {
                                    case R.id.rl_photo:
                                        if (user_id > 0) {
                                            view.setVisibility(View.VISIBLE);
                                        } else {
                                            view.setVisibility(View.GONE);
                                        }
                                        return true;
                                    case R.id.tv_title:
                                        ((TextView) view).setText(cursor.getString(cursor.getColumnIndex(Event.TITLE)));
                                        return true;
                                    case R.id.tv_text:
                                        ((TextView) view).setText(cursor.getString(cursor.getColumnIndex(Event.ANNOUNCEMENT_TEXT)));
                                        return true;
                                    case R.id.tv_name:
                                        new AsyncTask<Void, Void, String>() {
                                            @Override
                                            protected String doInBackground(Void... voids) {
                                                if (user_id < 1) return "Someone";
                                                Cursor c = getContentResolver().query(DBUtils.buildLocalUri(User.class), new String[]{User.LOCAL_ID, User.SV_ID, User.FIRST_NAME, User.LAST_NAME},
                                                        User.SV_ID + " = ?", new String[]{Long.toString(user_id)}, null);
                                                if (c.getCount() == 0) return "Someone";
                                                c.moveToFirst();
                                                String ret = c.getString(c.getColumnIndex(User.FIRST_NAME)) + " " + c.getString(c.getColumnIndex(User.LAST_NAME));
                                                c.close();
                                                return ret;
                                            }

                                            @Override
                                            protected void onPostExecute(String s) {
                                                ((TextView) view).setText(s);
                                            }
                                        }.execute();
                                        return true;
                                    case R.id.tv_action:
                                        ((TextView) view).setText("made an announcement");
                                        return true;
                                    case R.id.riv_user:
                                        if (user_id > 0)
                                            new AsyncTask<Void, Void, String>() {
                                                @Override
                                                protected String doInBackground(Void... voids) {
                                                    Cursor c = getContentResolver().query(DBUtils.buildLocalUri(User.class), new String[]{User.LOCAL_ID, User.SV_ID, User.THUMBNAIL, User.IMAGE_NAME},
                                                            User.SV_ID + " = ?", new String[]{Long.toString(user_id)}, null);
                                                    if (c.getCount() == 0) return null;
                                                    c.moveToFirst();
                                                    String ret = c.getString(c.getColumnIndex(User.IMAGE_NAME));
                                                    c.close();
                                                    return ret;
                                                }

                                                @Override
                                                protected void onPostExecute(String s) {
                                                    if (s != null && !s.isEmpty()) {
                                                        Picasso.with(FeedActivity.this)
                                                                .load(PeckApp.Constants.Network.BASE_URL + s)
                                                                .fit()
                                                                .centerCrop()
                                                                .into(((ImageView) view));
                                                    }
                                                }
                                            }.execute();
                                        return true;

                                    case R.id.rl_image:
                                        String nUrl = cursor.getString(cursor.getColumnIndex(Event.IMAGE_URL));
                                        if (nUrl == null || nUrl.isEmpty() || nUrl.equals("/images/missing.png")) {
                                            view.setVisibility(View.GONE);
                                        } else {
                                            view.setVisibility(View.VISIBLE);
                                        }
                                        return true;

                                    case R.id.iv_event:
                                        String url = cursor.getString(cursor.getColumnIndex(Event.IMAGE_URL));
                                        if (url != null && !url.isEmpty()) {
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
                            case Event.ATHLETIC_EVENT:
                                switch (view.getId()) {
                                    case R.id.rl_photo:
                                        view.setVisibility(View.GONE);
                                        return true;
                                    case R.id.tv_title:
                                        ((TextView) view).setText(cursor.getString(cursor.getColumnIndex(Event.ATHLETIC_OPPONENT)));
                                        return true;
                                    case R.id.tv_text:
                                        ((TextView) view).setText(cursor.getString(cursor.getColumnIndex(Event.ATHLETIC_NOTE)));
                                        return true;
                                    case R.id.tv_name:
                                        new AsyncTask<Void, Void, String>() {
                                            @Override
                                            protected String doInBackground(Void... voids) {
                                                if (user_id < 1) return "Someone";
                                                Cursor c = getContentResolver().query(DBUtils.buildLocalUri(User.class), new String[]{User.LOCAL_ID, User.SV_ID, User.FIRST_NAME, User.LAST_NAME},
                                                        User.SV_ID + " = ?", new String[]{Long.toString(user_id)}, null);
                                                if (c.getCount() == 0) return "Someone";
                                                c.moveToFirst();
                                                String ret = c.getString(c.getColumnIndex(User.FIRST_NAME)) + " " + c.getString(c.getColumnIndex(User.LAST_NAME));
                                                c.close();
                                                return ret;
                                            }

                                            @Override
                                            protected void onPostExecute(String s) {
                                                ((TextView) view).setText(s);
                                            }
                                        }.execute();

                                        return true;
                                    case R.id.tv_action:
                                        //todo: this is going to want to be a query. it'll only return an id at the moment.
                                        ((TextView) view).setText("is going to the game against " + cursor.getString(cursor.getColumnIndex(Event.ATHLETIC_OPPONENT)));
                                        return true;
                                    case R.id.riv_user:
                                        if (user_id > 0)
                                            new AsyncTask<Void, Void, String>() {
                                                @Override
                                                protected String doInBackground(Void... voids) {
                                                    Cursor c = getContentResolver().query(DBUtils.buildLocalUri(User.class), new String[]{User.LOCAL_ID, User.SV_ID, User.THUMBNAIL, User.IMAGE_NAME},
                                                            User.SV_ID + " = ?", new String[]{Long.toString(user_id)}, null);
                                                    if (c.getCount() == 0) return null;
                                                    c.moveToFirst();
                                                    String ret = c.getString(c.getColumnIndex(User.IMAGE_NAME));
                                                    c.close();
                                                    return ret;
                                                }

                                                @Override
                                                protected void onPostExecute(String s) {
                                                    if (s != null && !s.isEmpty()) {
                                                        Picasso.with(FeedActivity.this)
                                                                .load(PeckApp.Constants.Network.BASE_URL + s)
                                                                .fit()
                                                                .centerCrop()
                                                                .into(((ImageView) view));
                                                    }
                                                }
                                            }.execute();
                                        return true;

                                    case R.id.rl_image:
                                        view.setVisibility(View.GONE);
                                        return true;

                                    case R.id.iv_event:
                                        return true;
                                    default:
                                        return false;
                                }
                            default:
                                return false;
                        }

                    }
                }).build();
        buttons.put(R.id.bt_explore, feed);

        final LayoutInflater inflater = ((LayoutInflater) PeckApp.getContext().getSystemService(LAYOUT_INFLATER_SERVICE));

        //inflate the header for the circles feed
        View header = inflater.inflate(R.layout.circles_header, null, false);

        //with circle-adding functionality
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
                                            dialogView.findViewById(R.id.pb_network).setVisibility(View.GONE);
                                            if (object.get("errors").getAsJsonArray().size() > 0) {
                                                Toast.makeText(FeedActivity.this, "failure: " + object.get("errors").toString(), Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                Toast.makeText(FeedActivity.this, "success", Toast.LENGTH_LONG).show();
                                                DBUtils.syncJson(DBUtils.buildLocalUri(Circle.class), object.getAsJsonObject("circle"), Circle.class);
                                                dialogView.findViewById(R.id.pb_network).setVisibility(View.GONE);
                                                dialog.dismiss();
                                            }
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            Toast.makeText(FeedActivity.this, "Network error posting circle: " + ((error.getResponse() != null) ? (error.getResponse().getStatus() + " - " + error.getResponse().getReason()) : error.getMessage()), Toast.LENGTH_LONG).show();
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


        //use the instance tFeed field so we can access it in the builder. *do not* reassign this field.
        tFeed = new Feed.Builder(PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().appendPath(DBUtils.getTableName(Circle.class)).build(), R.layout.lvitem_circle)
                .withBindings(new String[]{Circle.NAME, Circle.MEMBERS, Circle.NAME},
                        new int[]{R.id.tv_title, R.id.hlv_users, R.id.bt_add_cm})
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    }
                })
                .withHeader(header)
                .orderedBy(DBOperable.UPDATED_AT + " desc")
                .withRecycleRunnable(new Feed.RecycleRunnable() {
                    @Override
                    public void run() {
                        //set/clear the search view as necessary
                        View mView = tFeed.getView();
                        if (mView != null && recycledView != null && recycledView.isFocusable() && (tFeed.getListView()).getPositionForView(recycledView) != currentCircleAddPos) {
                            ((LinearLayout) recycledView).addView(tView);
                        } else if (recycledView != null && tView != null)
                            ((LinearLayout) recycledView).removeView(tView);
                    }
                })
                .withViewBinder(
                        new SimpleCursorAdapter.ViewBinder() {
                            final SparseArray<ArrayList<Map<String, Object>>> circleMembers = new SparseArray<ArrayList<Map<String, Object>>>();

                            @Override
                            public boolean setViewValue(final View view, final Cursor cursor,
                                    int i) {
                                switch (view.getId()) {
                                    case R.id.hlv_users:
                                        final int circle_id = cursor.getInt(cursor.getColumnIndex(DBOperable.LOCAL_ID));
                                        new AsyncTask<Void, Void, Void>() {
                                            ArrayList<Map<String, Object>> ret = circleMembers.get(circle_id);
                                            SimpleAdapter simpleAdapter;

                                            @Override
                                            protected void onPreExecute() {
                                                setUpCell();
                                            }

                                            private void setUpCell() {
                                                synchronized (circleMembers) {
                                                    if (ret == null)
                                                        ret = new ArrayList<Map<String, Object>>();
                                                }

                                                simpleAdapter = new SimpleAdapter(FeedActivity.this, ret, R.layout.hlvitem_user,
                                                        new String[]{User.FIRST_NAME, User.IMAGE_NAME}, new int[]{R.id.tv_title, R.id.iv_event});

                                                ((HListView) view).setAdapter(simpleAdapter);
                                                simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                                                    @Override
                                                    public boolean setViewValue(View view, Object o,
                                                            String s) {
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
                                                        new String[]{User.FIRST_NAME, User.IMAGE_NAME, User.LOCAL_ID, User.SV_ID}, null, null, null);

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
                                                setUpCell();
                                            }
                                        }.execute();
                                        return true;
                                    case R.id.bt_add_cm:
                                        view.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        LinearLayout parent = ((LinearLayout) tView.getParent());
                                                                        if (parent != null) {
                                                                            parent.removeView(tView);
                                                                            if (parent.equals(view.getParent().getParent()))
                                                                                return;
                                                                        }

                                                                        tView.clearListSelection();
                                                                        ((SimpleCursorAdapter) tView.getAdapter()).changeCursor(null);
                                                                        tView.setText("");

                                                                        ((LinearLayout) view.getParent().getParent()).addView(tView);
                                                                        //tView.requestFocus();
                                                                        View feedView = tFeed.getView();
                                                                        if (feedView != null) {
                                                                            final ListView listView = ((ListView) tFeed.getListView());
                                                                            final int lvPos = listView.getPositionForView(tView);
                                                                            currentCircleAddPos = lvPos;
                                                                            listView.post(new Runnable() { //we post the movement as a runnable so the cell gets resized before it happens
                                                                                @Override
                                                                                public void run() {
                                                                                    final int pos = lvPos - listView.getFirstVisiblePosition();
                                                                                    final int offset = listView.getChildAt(pos).getMeasuredHeight() - tView.getMeasuredHeight();
                                                                                    listView.smoothScrollToPositionFromTop(lvPos, -offset);
                                                                                }
                                                                            });
                                                                            currentCircleId = listView.getAdapter().getItemId(currentCircleAddPos);
                                                                            new AsyncTask<Void, Void, Void>() {
                                                                                @Override
                                                                                protected Void doInBackground(
                                                                                        Void... voids) {
                                                                                    ((SimpleCursorAdapter) tView.getAdapter()).changeCursor(FeedActivity.this.getContentResolver().query(DBUtils.buildLocalUri(Circle.class)
                                                                                            .buildUpon().appendPath(Long.toString(currentCircleId)).appendPath("nonmembers").build(),
                                                                                            new String[]{ User.LOCAL_ID, User.SV_ID, User.FIRST_NAME, User.IMAGE_NAME, User.THUMBNAIL}, null, null, User.FIRST_NAME ));
                                                                                    return null;
                                                                                }
                                                                            }.execute();
                                                                        } else {
                                                                            throw new IllegalStateException("onclick was called when fragment was unavailable.");
                                                                        }



                                                                        Log.d(FeedActivity.class.getSimpleName(), "clicked");
                                                                    }
                                                                }

                                        );
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        }

                ).build();

        buttons.put(R.id.bt_circles, tFeed);

        //set up the peck feed
        feed = new Feed.Builder(DBUtils.buildLocalUri(Peck.class), R.layout.lvitem_peck)
                .withBindings(new String[] {Peck.TEXT, Peck.INVITED_BY}, new int[] { R.id.tv_title, R.id.iv_def } )
                .withViewBinder(new SimpleCursorAdapter.ViewBinder() {
                                    @Override
                                    public boolean setViewValue ( final View view, Cursor cursor,int i){
                                        switch (view.getId()) {
                                            case R.id.iv_def:
                                                final long userId = cursor.getLong(i);
                                                if (userId > 0) {
                                                    new AsyncTask<Void, Void, String>() {
                                                        @Override
                                                        protected String doInBackground(Void... voids) {
                                                            String url = null;
                                                            Cursor user = getContentResolver().query(DBUtils.buildLocalUri(User.class), new String[]{User.LOCAL_ID, User.SV_ID, User.THUMBNAIL, User.IMAGE_NAME},
                                                                    User.SV_ID + "= ?", new String[]{Long.toString(userId)}, null);
                                                            if (user.getCount() > 0) {
                                                                user.moveToFirst();
                                                                url = user.getString(user.getColumnIndex(User.IMAGE_NAME));
                                                            }
                                                            user.close();
                                                            return url;
                                                        }

                                                        @Override
                                                        protected void onPostExecute(String url) {
                                                            if (url != null) {
                                                                Picasso.with(FeedActivity.this)
                                                                        .load(PeckApp.Constants.Network.BASE_URL + url)
                                                                        .centerCrop()
                                                                        .fit()
                                                                        .into(((RoundedImageView) view));
                                                            }

                                                        }
                                                    }.execute();
                                                }
                                                return true;
                                            default:
                                                return false;
                                        }
                                    }
                                }

                ).build();

        buttons.put(R.id.bt_peck,feed);

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

        //build the event feed
        Feed feed = new Feed.Builder(PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().appendPath(DBUtils.getTableName(Event.class)).build(), R.layout.lvitem_event)
                .withBindings(new String[]{Event.TYPE, Event.TYPE, Event.IMAGE_URL, Event.START_DATE}, new int[]{R.id.tv_title, R.id.tv_text, R.id.iv_event, R.id.tv_time})
                .withProjection(new String[]{Event.TITLE, Event.TEXT, Event.ATHLETIC_OPPONENT, Event.DINING_OP_TYPE, DBOperable.LOCAL_ID, Event.TYPE,
                        Event.DINING_START_TIME, Event.DINING_END_TIME, Event.IMAGE_URL, Event.ANNOUNCEMENT_TEXT, Event.UPDATED_AT, Event.BLURRED_URL,
                        Event.ATHLETIC_DATE_AND_TIME, Event.START_DATE, Event.DINING_START_TIME})
                .orderedBy(Event.UPDATED_AT + " desc")
                .withoutDividers()
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, final long l) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                Cursor temp = getContentResolver().query(DBUtils.buildLocalUri(Event.class), new String[] { Event.LOCAL_ID, Event.TYPE }, Event.LOCAL_ID + " = ?", new String[] { Long.toString(l) }, null);
                                temp.moveToFirst();
                                String type = temp.getString(temp.getColumnIndex(Event.TYPE));
                                Log.d(FeedActivity.class.getSimpleName(), type.equals("0") ? "simple" : type.equals("3") ? "announcement" : type.equals("1") ? "athletic" : "dining op");
                                temp.close();
                                return null;
                            }
                        }.execute();
                    }
                })
                .withViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(final View view, final Cursor cursor, int i) {
                        switch (cursor.getInt(cursor.getColumnIndex(Event.TYPE))) {
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
                                                .fit()
                                                .centerCrop()
                                                .into((ImageView) view);
                                        return true;
                                    case R.id.tv_time:
                                        long date = cursor.getLong(cursor.getColumnIndex(Event.ATHLETIC_DATE_AND_TIME))*1000l;
                                        if (date > 0)
                                            ((TextView) view).setText(new DateTime(date).toDateTime(DateTimeZone.forTimeZone(tz)).toString("K:mm"));
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
                                        return true;
                                    case R.id.iv_event:
                                        Picasso.with(FeedActivity.this)
                                                .load(R.drawable.ic_peck)
                                                .fit()
                                                .centerCrop()
                                                .into((ImageView) view);
                                        return true;
                                    case R.id.tv_time:
                                        DateTime start = new DateTime(cursor.getLong(cursor.getColumnIndex(Event.DINING_START_TIME))*1000l).toDateTime(DateTimeZone.forTimeZone(tz));
                                        ((TextView) view).setText(start.toString("K:mm"));
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
                                        String urlPath = cursor.getString(cursor.getColumnIndex(Event.BLURRED_URL));
                                        if (urlPath != null && urlPath.length() != 0) {
                                            Picasso.with(FeedActivity.this)
                                                    .load(PeckApp.Constants.Network.BASE_URL + urlPath)
                                                    .fit()
                                                    .centerCrop()
                                                    .into((ImageView) view);
                                        } else {
                                            Picasso.with(FeedActivity.this)
                                                    .load(R.drawable.ic_peck)
                                                    .fit()
                                                    .centerCrop()
                                                    .into((ImageView) view);
                                        }
                                        return true;
                                    case R.id.tv_time:
                                        DateTime stTemp = new DateTime(cursor.getLong(cursor.getColumnIndex(Event.START_DATE))*1000l).toDateTime(DateTimeZone.forTimeZone(tz));
                                        ((TextView) view).setText(stTemp.toString("K:mm"));
                                        return true;
                                    default:
                                        return false;
                                }
                            case Event.ANNOUNCEMENT:
                                switch (view.getId()) {
                                    case R.id.tv_title:
                                        ((TextView) view).setText(cursor.getString(cursor.getColumnIndex(Event.TITLE)));
                                        return true;
                                    case R.id.tv_text:
                                        ((TextView) view).setText(cursor.getString(cursor.getColumnIndex(Event.ANNOUNCEMENT_TEXT)));
                                        return true;
                                    case R.id.tv_time:
                                        ((TextView) view).setText("");
                                        return true;
                                    case R.id.iv_event:
                                        Picasso.with(FeedActivity.this)
                                                .load(R.drawable.ic_peck)
                                                .fit()
                                                .centerCrop()
                                                .into(((ImageView) view));
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

    /**
     * an attempt to make a thread-safe adapter for a spinner.
     * built for search view.
     */
    private class SearchSpinnerAdapter extends BaseAdapter {
        private Handler handler = new Handler(Looper.getMainLooper());
        private LinkedHashMap<String, Long> data = new LinkedHashMap<String, Long>();

        private SearchSpinnerAdapter() {

        }


        private SearchSpinnerAdapter(LinkedHashMap<String, Long> data) {
            this.data = data;
        }

        public synchronized void setData(LinkedHashMap<String, Long> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public synchronized int getCount() {
            return data.size();
        }

        /**
         * @param i position position to check
         * @return the string at position i
         */
        @Override
        public synchronized String getItem(int i) {
            return ((String) data.keySet().toArray()[i]);
        }

        /**
         * @param i position to check
         * @return the id of the item at position i
         */
        @Override
        public synchronized long getItemId(int i) {
            return data.get(getItem(i));
        }

        @Override
        public synchronized View getView(int i, View convertView, ViewGroup viewGroup) {
            if (convertView == null) {
                convertView = ((LayoutInflater) PeckApp.getContext().getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.sp_search_item, viewGroup);
            }

            ((TextView) convertView.findViewById(R.id.tv_title)).setText(getItem(i));
            return convertView;
        }


        /**
         * execute on main thread. we'll get errors if we call from an async thread.
         */
        @Override
        public synchronized void notifyDataSetChanged() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    SearchSpinnerAdapter.super.notifyDataSetChanged();
                }
            });
        }
    }


}
