/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android.fragments.feeds;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.database.DBUtils;
import com.peck.android.fragments.Feed;
import com.peck.android.models.DBOperable;
import com.peck.android.models.Event;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Created by mammothbane on 8/14/2014.
 */
public class HomeFeed extends Feed {
    public static final String DATE_ORDER = "date_order";
    public static final String SEL_BY_DATE = DATE_ORDER + " > ? and " + DATE_ORDER + " < ?";
    private String baseSelection;


    public int curOffset = -1;
    {
        listItemRes = R.layout.lvitem_event;
        binds_from = new String[]{Event.TYPE, Event.TYPE, Event.IMAGE_URL, Event.START_DATE};
        binds_to = new int[] {R.id.tv_title, R.id.tv_text, R.id.iv_event, R.id.tv_time};
        loaderBundle = new Bundle();
        loaderBundle.putString(LOADER_SORT_ORDER, Event.START_DATE + " desc");
        loaderBundle.putStringArray(LOADER_PROJECTION, new String[]{Event.TITLE, Event.TEXT, Event.ATHLETIC_OPPONENT, Event.DINING_OP_TYPE, DBOperable.LOCAL_ID, Event.TYPE,
                Event.DINING_START_TIME, Event.DINING_END_TIME, Event.IMAGE_URL, Event.ANNOUNCEMENT_TEXT, Event.UPDATED_AT, Event.BLURRED_URL,
                Event.ATHLETIC_DATE_AND_TIME, Event.START_DATE, Event.DINING_START_TIME,
                "case " + Event.TYPE + " when " + Event.SIMPLE_EVENT + " then " + Event.START_DATE +
                        " when " + Event.DINING_OPPORTUNITY + " then " + Event.DINING_START_TIME +
                        " when " + Event.ATHLETIC_EVENT + " then " + Event.ATHLETIC_DATE_AND_TIME +
                        " when " + Event.ANNOUNCEMENT + " then " + Event.UPDATED_AT + " end as " + DATE_ORDER});
        loaderBundle.putParcelable(LOADER_URI, DBUtils.buildLocalUri(Event.class));
        dividers = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, final long l) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        Cursor temp = getActivity().getContentResolver().query(DBUtils.buildLocalUri(Event.class), new String[]{Event.LOCAL_ID, Event.TYPE}, Event.LOCAL_ID + " = ?", new String[]{Long.toString(l)}, null);
                        temp.moveToFirst();
                        String type = temp.getString(temp.getColumnIndex(Event.TYPE));
                        Log.d(HomeFeed.class.getSimpleName(), type.equals("0") ? "simple" : type.equals("3") ? "announcement" : type.equals("1") ? "athletic" : "dining op");
                        temp.close();
                        return null;
                    }
                }.execute();
            }
        };
        viewBinder = new SimpleCursorAdapter.ViewBinder() {
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
                                return true;
                            case R.id.tv_time:
                                long date = cursor.getLong(cursor.getColumnIndex(Event.ATHLETIC_DATE_AND_TIME))*1000l;
                                if (date > 0)
                                    ((TextView) view).setText(new DateTime(date).toDateTime(DateTimeZone.forTimeZone(PeckApp.tz)).toString("K:mm"));
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
                                return true;
                            case R.id.tv_time:
                                DateTime start = new DateTime(cursor.getLong(cursor.getColumnIndex(Event.DINING_START_TIME))*1000l).toDateTime(DateTimeZone.forTimeZone(PeckApp.tz));
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
                                if (urlPath != null && urlPath.length() != 0 && !urlPath.equals("/images/missing.png")) {
                                    Picasso.with(getActivity())
                                            .load(PeckApp.Constants.Network.BASE_URL + urlPath)
                                            .fit()
                                            .centerCrop()
                                            .into((ImageView) view);
                                }
                                return true;
                            case R.id.tv_time:
                                DateTime stTemp = new DateTime(cursor.getLong(cursor.getColumnIndex(Event.START_DATE))*1000l).toDateTime(DateTimeZone.forTimeZone(PeckApp.tz));
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
                                return true;
                            default:
                                return false;
                        }
                }
                return false;
            }
        };
        withRelativeDate(0);
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        baseSelection = loaderBundle.getString(LOADER_SELECTION);
    }

    public void withRelativeDate(int offset) {
        if (curOffset != offset) {
            String sel = SEL_BY_DATE;
            long after = DateTime.now().plusDays(offset).withTimeAtStartOfDay().toInstant().getMillis() / 1000l;
            long before = DateTime.now().plusDays(offset + 1).withTimeAtStartOfDay().toInstant().getMillis() / 1000l;
            sel = sel.replaceFirst("\\?", Long.toString(after));
            sel = sel.replace("?", Long.toString(before));
            setSelection(sel, null);

            curOffset = offset;
        } else Log.v(HomeFeed.class.getSimpleName(), "not updating this fragment with offset " + offset + "; it's already there");
    }
}
