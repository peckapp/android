package com.peck.android;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.PeckSessionManager;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class PeckApp extends Application implements Singleton{

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

        public static Context getContext() {
            return mContext;
        }
    }

    public void onCreate() {
        AppContext.init(this);
        PeckSessionManager.init();
    }


    public static class Constants {

        public static class Food {


            public final static int BREAKFAST = 1;
            public final static int LUNCH = 2;
            public final static int DINNER = 3;
            public final static int NIGHT_MEAL = 4;
        }

        public static class Preferences {
            public final static String USER_PREFS = "user preferences";
            public final static String USER_ID = "persistent user id";

        }


        public static class Database {

            public static final String DATABASE_NAME = "peck.db";
            public final static int RETRY = 20;
            public final static int UI_TIMEOUT = 50;
            public final static int QUEUE_TIMEOUT = 1000;
        }

        public static class Location {

            public final static int INTERVAL = 300;
            public final static int RETRY = 33;

        }

        public static class Graphics {

            public final static int FILLER = R.drawable.ic_peck;
            public final static int CACHE_SIZE = 20;
            public final static int INT_CACHE_SIZE = 50;
            public final static int PNG_COMPRESSION = 90;

        }

    }


    private static RequestQueue requestQueue;

    public static RequestQueue getRequestQueue() {
        if (requestQueue == null) requestQueue = Volley.newRequestQueue(AppContext.getContext());
        return requestQueue;
    }

}
