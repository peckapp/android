package com.peck.android;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import com.peck.android.annotations.Header;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.FacebookSessionHandler;
import com.peck.android.models.Circle;
import com.peck.android.models.Club;
import com.peck.android.models.Comment;
import com.peck.android.models.Department;
import com.peck.android.models.DiningPlace;
import com.peck.android.models.Event;
import com.peck.android.models.Locale;
import com.peck.android.models.MenuItem;
import com.peck.android.models.Peck;
import com.peck.android.models.Subscription;
import com.peck.android.models.User;
import com.peck.android.models.joins.CircleMember;
import com.peck.android.models.joins.EventAttendee;
import com.squareup.picasso.Picasso;

/**
 * Created by mammothbane on 5/28/2014.
 *
 * the base application, created when the app starts.
 *
 */
public class PeckApp extends Application implements Singleton{

    private static Context mContext;


    public static Context getContext() {
        return mContext;
    }

    public static final String AUTHORITY = "com.peck.android.provider.all";

    private static final Class[] MODELS = { Circle.class, Event.class, Locale.class, Peck.class, Comment.class, User.class, DiningPlace.class, Subscription.class,
            CircleMember.class, EventAttendee.class, Department.class, MenuItem.class, Club.class};


    public static Class[] getModelArray() {
        return MODELS;
    }

    public static String buildEndpointURL(Class tClass) {
        Header header = (Header)tClass.getAnnotation(Header.class);
        if (BuildConfig.DEBUG && (header == null || header.singular() == null || header.plural() == null)) throw new IllegalArgumentException(tClass.getSimpleName() + " does not have a header");
        return Constants.Network.API_ENDPOINT + header.plural() + "/";
    }

    public void onCreate() {

        //StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        mContext = this;
        //Crashlytics.start(this);
       /* NewRelic.withApplicationToken(
                "AAb263b9d104b0c100c64a79f2c229cef86daf51a1"
        ).start(this);*/

        System.setProperty("org.joda.time.DateTimeZone.Provider",
                "com.peck.android.FastDateTimeZoneProvider");

        if (BuildConfig.DEBUG) {
            Picasso.with(getContext()).setIndicatorsEnabled(true);
            //Picasso.with(getContext()).setLoggingEnabled(true);
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
            public final static String BASE_URL = "http://loki.peckapp.com:3500";
            public final static String API_ENDPOINT = BASE_URL + "/api/";

        }

        public final static class Preferences {
            public final static String USER_PREFS = "user preferences";
            public final static String LOCALE_ID = "persistent locale id";
        }


        public final static class Database {
            public static final Uri BASE_AUTHORITY_URI = Uri.parse("content://com.peck.android.provider.all");
            public static final String DATABASE_NAME = "peck.db";
        }

        public final static class Location {

            public final static int INTERVAL = 300;
            public final static int RETRY = 33;

        }

        public final static class Graphics {

            public final static int FILLER = R.drawable.ic_peck;
            public final static int CACHE_SIZE = 5*1024*1024; //5MB cache maximum
            public final static int INT_CACHE_SIZE = 50;
            public final static int PNG_COMPRESSION = 90;

        }

    }
}
