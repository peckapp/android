package com.peck.android;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class PeckApp extends Application {

    public static class Constants {

        public final static int BREAKFAST = 1;
        public final static int LUNCH = 2;
        public final static int DINNER = 3;
        public final static int NIGHT_MEAL = 4;

    }

    private RequestQueue requestQueue;
    private static final String DATABASE_NAME = "peck.db";

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) requestQueue = Volley.newRequestQueue(this);
        return requestQueue;
    }

}
