package com.peck.android;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class PeckApp extends Application {

    private RequestQueue requestQueue;
    private static final String DATABASE_NAME = "peck.db";

    public static String getDatabaseName() {
        return DATABASE_NAME;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) requestQueue = Volley.newRequestQueue(this);
        return requestQueue;
    }

}
