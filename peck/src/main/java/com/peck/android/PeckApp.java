/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.StrictMode;

import com.peck.android.managers.FacebookSessionHandler;
import com.peck.android.models.Club;
import com.peck.android.models.Department;
import com.peck.android.models.DiningPlace;
import com.peck.android.models.Event;
import com.peck.android.models.Locale;
import com.peck.android.models.MenuItem;
import com.peck.android.models.Subscription;
import com.peck.android.models.joins.EventAttendee;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by mammothbane on 5/28/2014.
 *
 * the base application, created when the app starts.
 *
 */
public class PeckApp extends Application {

    //the app version
    public static final double version = 1.0;

    private static Context mContext;
    public static TimeZone tz = Calendar.getInstance().getTimeZone();


    /**
     * the global application context
     */
    public static Context getContext() {
        return mContext;
    }


    public static final String AUTHORITY = "com.peck.android.provider.all";

    /* FIXME  -  COMMENTED FOR RELEASE

    private static final Class[] MODELS = { Circle.class, Event.class, Locale.class, Peck.class, Comment.class, User.class, DiningPlace.class, Subscription.class,
            CircleMember.class, EventAttendee.class, Department.class, MenuItem.class, Club.class}; */

    private static final Class[] MODELS = { Event.class, Locale.class, DiningPlace.class, Subscription.class,
            EventAttendee.class, Department.class, MenuItem.class, Club.class};


    public static Class[] getModelArray() {
        return MODELS;
    }

    public void onCreate() {
        mContext = this;

        /*NewRelic.withApplicationToken(
                "AAb263b9d104b0c100c64a79f2c229cef86daf51a1"
        ).start(this);*/

        System.setProperty("org.joda.time.DateTimeZone.Provider",
                "com.peck.android.FastDateTimeZoneProvider");

        if (BuildConfig.DEBUG) {
            Picasso.with(getContext()).setIndicatorsEnabled(true);
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().penaltyLog().detectLeakedClosableObjects().build());
        }

        FacebookSessionHandler.init();
    }



    public static class Constants {

        public final static class Network {

            public final static int RETRY_INTERVAL = 200;
            public final static int CONNECT_TIMEOUT = 10000;
            public final static int READ_TIMEOUT = 6000;

            public final static long POLL_FREQUENCY = 60L;

            public final static long LOW_PRIORITY_POLL_FREQUENCY = 1000L*60L*5L;

            /**
             * API strings
             */
            public final static String BASE_URL = BuildConfig.DEBUG ? "http://loki.peckapp.com:3500" : "http://yggdrasil.peckapp.com:3500";

        }

        public final static class Preferences {
            public final static String USER_PREFS = "user preferences";
            public final static String LOCALE_ID = "persistent locale id";
        }


        public final static class Database {
            public static final Uri BASE_AUTHORITY_URI = Uri.parse("content://com.peck.android.provider.all");
            public static final String DATABASE_NAME = "peck.db";
        }

    }
}
