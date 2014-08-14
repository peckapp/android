/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android.fragments.feeds;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.database.DBUtils;
import com.peck.android.fragments.Feed;
import com.peck.android.interfaces.FailureCallback;
import com.peck.android.managers.LoginManager;
import com.peck.android.models.DBOperable;
import com.peck.android.models.Event;
import com.peck.android.models.User;
import com.peck.android.network.JsonUtils;
import com.peck.android.network.PeckSyncAdapter;
import com.peck.android.network.ServerCommunicator;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mammothbane on 8/14/2014.
 *
 * explore feed. makes a web call to update suggested events.
 * grabs 200 most recently explore-updated items and displays them.
 *
 * @author mammothbane
 * @since 1.0
 *
 */
public class ExploreFeed extends Feed {

    public static final String DATE_ORDER = "explr_order";
    private boolean isRefreshing = false;
    private final Object refreshLock = new Object();
    {
        listItemRes = R.layout.lvitem_explore;
        binds_from = new String[]{Event.TITLE, Event.IMAGE_URL, Event.IMAGE_URL, Event.IMAGE_URL, Event.USER_ID, Event.TEXT, Event.START_DATE, Event.USER_ID, Event.USER_ID, Event.USER_ID};
        binds_to = new int[]{R.id.tv_title, R.id.iv_event, R.id.rl_image, R.id.riv_user, R.id.tv_text, R.id.tv_time, R.id.tv_name, R.id.tv_action, R.id.rl_photo};
        loaderBundle = new Bundle();
        loaderBundle.putParcelable(LOADER_URI, DBUtils.buildLocalUri(Event.class));
        loaderBundle.putStringArray(LOADER_PROJECTION, new String[]{Event.SCORE, Event.SCORE_UPDATED, Event.IMAGE_URL, Event.USER_ID, Event.TITLE, Event.TYPE, Event.TEXT, Event.START_DATE, Event.USER_ID,
                Event.LOCAL_ID, Event.SR_ID, Event.UPDATED_AT,
                Event.ANNOUNCEMENT_TEXT, Event.ANNOUNCEMENT_COMMENT_COUNT,
                Event.ATHLETIC_OPPONENT, Event.ATHLETIC_DATE_AND_TIME, Event.ATHLETIC_NOTE, Event.ATHLETIC_HOME_SCORE, Event.ATHLETIC_HOME_AWAY, Event.ATHLETIC_AWAY_SCORE, Event.ATHLETIC_LOCATION, "case " + Event.TYPE + " when " + Event.SIMPLE_EVENT + " then " + Event.START_DATE +
                " when " + Event.ATHLETIC_EVENT + " then " + Event.ATHLETIC_DATE_AND_TIME +
                " when " + Event.ANNOUNCEMENT + " then " + Event.UPDATED_AT + " end as " + DATE_ORDER});

        loaderBundle.putString(LOADER_SELECTION, Event.TYPE + " = ? OR " + Event.TYPE + " = ? OR " + Event.TYPE + " = ? AND " + Event.END_DATE + " > " + DateTime.now().toInstant().getMillis()/1000L);
        loaderBundle.putStringArray(LOADER_SELECT_ARGS, new String[]{Integer.toString(Event.SIMPLE_EVENT), Integer.toString(Event.ATHLETIC_EVENT), Integer.toString(Event.ANNOUNCEMENT)});
        loaderBundle.putString(LOADER_SORT_ORDER, Event.SCORE_UPDATED + " desc, " +  Event.SCORE + " desc, " + DATE_ORDER + " asc limit 200");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinder = new SimpleCursorAdapter.ViewBinder() {
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
                                    Picasso.with(getActivity())
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
                                            Cursor c = getActivity().getContentResolver().query(DBUtils.buildLocalUri(User.class), new String[]{User.LOCAL_ID, User.SV_ID, User.THUMBNAIL, User.IMAGE_NAME},
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
                                                Picasso.with(getActivity())
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
                                    ((TextView) view).setText(new DateTime(l).toDateTime(DateTimeZone.forTimeZone(PeckApp.tz)).toString("MMM d"));
                                else ((TextView) view).setText("");
                                return true;
                            case R.id.tv_name:
                                new AsyncTask<Void, Void, String>() {
                                    @Override
                                    protected String doInBackground(Void... voids) {
                                        if (user_id < 1) return "Someone";
                                        Cursor c = getActivity().getContentResolver().query(DBUtils.buildLocalUri(User.class), new String[]{User.LOCAL_ID, User.SV_ID, User.FIRST_NAME, User.LAST_NAME},
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
                                        Cursor c = getActivity().getContentResolver().query(DBUtils.buildLocalUri(User.class), new String[]{User.LOCAL_ID, User.SV_ID, User.FIRST_NAME, User.LAST_NAME},
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
                                            Cursor c = getActivity().getContentResolver().query(DBUtils.buildLocalUri(User.class), new String[]{User.LOCAL_ID, User.SV_ID, User.THUMBNAIL, User.IMAGE_NAME},
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
                                                Picasso.with(getActivity())
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
                                    Picasso.with(getActivity())
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
                                        Cursor c = getActivity().getContentResolver().query(DBUtils.buildLocalUri(User.class), new String[]{User.LOCAL_ID, User.SV_ID, User.FIRST_NAME, User.LAST_NAME},
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
                                            Cursor c = getActivity().getContentResolver().query(DBUtils.buildLocalUri(User.class), new String[]{User.LOCAL_ID, User.SV_ID, User.THUMBNAIL, User.IMAGE_NAME},
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
                                                Picasso.with(getActivity())
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
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        //refresh();
    }

    public void refresh() {
        synchronized (refreshLock) { //fixme: this is a really rudimentary way of making this work
            if (isRefreshing) return;
            isRefreshing = true;
        }
        JsonUtils.auth(LoginManager.getActive(), new FailureCallback<Map<String, String>>() {
            @Override
            protected void success(Map<String, String> item) {
                ServerCommunicator.jsonService.updateExplore(item, new Callback<JsonObject>() {
                    @Override
                    public void success(final JsonObject jsonObject, Response response) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                handleExploreArray(jsonObject.getAsJsonArray("explore_events"), false);
                                handleExploreArray(jsonObject.getAsJsonArray("explore_announcements"), true);
                                synchronized (refreshLock) { isRefreshing = false; }
                                return null;
                            }
                        }.execute();

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(ExploreFeed.class.getSimpleName(), "[ ERROR " + StringUtils.substring(error.getResponse() != null ? Integer.toString(error.getResponse().getStatus()) : "???", 0, 2) + "] Failed to download new explore items.", error);
                        synchronized (refreshLock) { isRefreshing = false; }
                    }
                });

            }

            @Override
            protected void failure(Throwable cause) {
                Log.e(ExploreFeed.class.getSimpleName(), "Failed to authenticate your account on explore refresh.", cause);
                synchronized (refreshLock) { isRefreshing = false; }
            }
        });
    }




    private void handleExploreArray(@Nullable JsonArray unwrappedArray, boolean isAnnouncement) {
        if (unwrappedArray == null || unwrappedArray.isJsonNull() || unwrappedArray.size() == 0) return;
        double updatedTime = DateTime.now().toInstant().getMillis()/1000D;
        Cursor cursor = PeckApp.getContext().getContentResolver().query(DBUtils.buildLocalUri(Event.class),
                new String[] { Event.SV_ID, Event.UPDATED_AT, Event.TYPE, Event.LOCAL_ID }, null, null, DBOperable.UPDATED_AT);
        HashMap<Long, JsonObject> map = new HashMap<Long, JsonObject>();
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
        for (JsonElement element : unwrappedArray) {
            map.put(element.getAsJsonObject().get(Event.SV_ID).getAsLong(), element.getAsJsonObject());
        }

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(Event.SV_ID));
            if (map.containsKey(id) && cursor.getDouble(cursor.getColumnIndex(Event.UPDATED_AT)) <= map.get(id).get(Event.UPDATED_AT).getAsDouble()) {
                JsonObject object = map.remove(id); //remove so it doesn't get inserted later
                ContentValues values = JsonUtils.jsonToContentValues(object, Event.class);
                values.put(Event.TYPE, isAnnouncement ? Event.ANNOUNCEMENT : object.get("event_type").getAsString().equals("simple") ? Event.SIMPLE_EVENT : Event.ATHLETIC_EVENT);
                values.put(Event.SCORE_UPDATED, updatedTime);
                batch.add(ContentProviderOperation.newUpdate(DBUtils.buildLocalUri(Event.class)).withValues(values).build());
            }
        }

        for (JsonObject object : map.values()) { //insert the remaining values
            ContentValues values = JsonUtils.jsonToContentValues(object, Event.class);
            values.put(Event.TYPE, isAnnouncement ? Event.ANNOUNCEMENT : object.get("event_type").getAsString().equals("simple") ? Event.SIMPLE_EVENT : Event.ATHLETIC_EVENT);
            values.put(Event.SCORE_UPDATED, updatedTime);
            batch.add(ContentProviderOperation.newInsert(DBUtils.buildLocalUri(Event.class)).withValues(values).build());
        }

        try {
            synchronized (PeckSyncAdapter.batchLock) {
                PeckApp.getContext().getContentResolver().applyBatch(PeckApp.AUTHORITY, batch);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
        PeckApp.getContext().getContentResolver().notifyChange(DBUtils.buildLocalUri(Event.class), null, false);
    }


}
