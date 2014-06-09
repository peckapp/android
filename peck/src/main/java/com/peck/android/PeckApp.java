package com.peck.android;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.peck.android.factories.EventFactory;
import com.peck.android.factories.GenericFactory;

import java.util.HashMap;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class PeckApp extends Application {

    private RequestQueue requestQueue;
    private HashMap<Class, GenericFactory> map = new HashMap<Class, GenericFactory>();


    public EventFactory getEventFactory() {
        return EventFactory.getFactory();
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) requestQueue = Volley.newRequestQueue(this);
        return requestQueue;
    }

}
