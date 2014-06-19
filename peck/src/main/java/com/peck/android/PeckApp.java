package com.peck.android;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.peck.android.interfaces.Singleton;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class PeckApp extends Application implements Singleton{
    private static PeckApp peckApp = new PeckApp();

    private PeckApp() {

    }

    public static PeckApp getApp() {
        return peckApp;
    }

    public final static String USER_PREFS = "user preferences";

    public static class Constants {

        public static class Food {


            public final static int BREAKFAST = 1;
            public final static int LUNCH = 2;
            public final static int DINNER = 3;
            public final static int NIGHT_MEAL = 4;
        }


        public static class Database {

            public static final String DATABASE_NAME = "peck.db";
            public final static int RETRY = 20;
            public final static int UI_TIMEOUT = 50;
        }

        public static class Location {

            public final static int INTERVAL = 300;
            public final static int RETRY = 33;

        }


    }



    private RequestQueue requestQueue;

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) requestQueue = Volley.newRequestQueue(this);
        return requestQueue;
    }

}
