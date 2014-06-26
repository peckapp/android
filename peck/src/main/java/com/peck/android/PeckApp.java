package com.peck.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.PeckSessionManager;
import com.peck.android.models.Event;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

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

        Log.d("qApp", "adding request");
        getRequestQueue().add(new JsonObjectRequest(Request.Method.GET, Constants.Network.API_STRING + Constants.Network.EVENTS, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(AppContext.getContext(), "help", Toast.LENGTH_LONG).show();
            }
        }));

        Event event = new Event();
        event.setTitle("test event - np");
        event.setStartTime(new Date(System.currentTimeMillis()));
        event.setEndTime(new Date(System.currentTimeMillis() + 30000));

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        JSONObject item = new JSONObject();
        try {
            item = new JSONObject(gson.toJson(event));
            item.put("institution_id", 30);
        } catch (JSONException e) { e.printStackTrace(); }

        getRequestQueue().add(new JsonObjectRequest(Request.Method.POST, Constants.Network.API_STRING + Constants.Network.EVENTS, item, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                Log.d(getClass().getSimpleName(), object.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(getClass().getSimpleName(), "help");
            }
        }));
    }


    public static class Constants {

        public final static class Network {
            public final static String API_STRING = "http://thor.peckapp.com:3500/api/";
            public final static String EVENTS = "simple_events";
            public final static String API_TEST_KEY = "";
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
            public final static int RETRY = 20;
            public final static int UI_TIMEOUT = 50;
            public final static int QUEUE_TIMEOUT = 1000;
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
        if (requestQueue == null) requestQueue = Volley.newRequestQueue(AppContext.getContext());
        return requestQueue;
    }

}
