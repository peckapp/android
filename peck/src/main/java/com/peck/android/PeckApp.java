package com.peck.android;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.PeckSessionHandler;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * Created by mammothbane on 5/28/2014.
 *
 * the base app, created when the app starts.
 *
 */
public class PeckApp extends Application implements Singleton{


    public static Context getContext() {
        return AppContext.mContext;
    }

    public static class AppContext {
        private static Context mContext;
        private static AppContext appContext;

        private AppContext() {}

        protected static void init(Context context) {
            mContext = context;
        }

        public static AppContext getAppContext() {
            return appContext;
        }

    }

    public void onCreate() {

        AppContext.init(this);
        PeckSessionHandler.init();


    }


    public static class Constants {

        public final static class Network {
            public final static String SV_ID_NAME = "id";

            public final static int RETRY_INTERVAL = 200;
            public final static int TIMEOUT = 6000;

            public final static String API_STRING = "http://thor.peckapp.com:3500/api/";
            public final static String EVENTS = "simple_events/";
            public final static String CIRCLES = "circles/";
            public final static String USERS = "users/";
            public final static String LOCALES = "institutions/";


            //todo: get these:
            public final static String MEAL = null;
            public final static String FOOD = null;
            public final static String PECK = null;

            public final static String API_TEST_KEY = "";


            public final static String INSTITUTION = "institution_id";

        }

        public final static class Food {


            public final static int BREAKFAST = 1;
            public final static int LUNCH = 2;
            public final static int DINNER = 3;
            public final static int NIGHT_MEAL = 4;
        }

        public final static class Preferences {
            public final static String USER_PREFS = "user preferences";
            public final static String USER_ID = "persistent user id";

        }


        public final static class Database {

            public static final String DATABASE_NAME = "peck.db";

            public final static String LOCAL_ID = "localId";


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


    private static RequestQueue requestQueue;

    public static RequestQueue getRequestQueue() {
        if (requestQueue == null) requestQueue = Volley.newRequestQueue(getContext(),
                new HttpClientStack(HttpClients.custom().setConnectionManager(new PoolingHttpClientConnectionManager()).build()));
        return requestQueue;
    }
}
