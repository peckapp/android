package com.peck.android;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.peck.android.database.DataSource;
import com.peck.android.database.dataspec.CirclesDataSpec;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;
import com.peck.android.json.JsonConverter;
import com.peck.android.managers.PeckSessionManager;
import com.peck.android.models.Circle;
import com.peck.android.models.Event;
import com.peck.android.network.NetworkSpec.EventSpec;
import com.peck.android.network.ServerCommunicator;

import java.util.ArrayList;
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

        Event event = new Event();
        event.setTitle("test event - np");
        event.setStartTime(new Date(System.currentTimeMillis()));
        event.setEndTime(new Date(System.currentTimeMillis() + 30000));

        ServerCommunicator.getAll(new EventSpec(), new Callback<ArrayList<Event>>() {
            @Override
            public void callBack(ArrayList<Event> obj) {
                Log.d("peckapp", obj.toString());
            }
        });

        ServerCommunicator.getObject(3, new EventSpec(), new Callback<Event>() {
            @Override
            public void callBack(Event obj) {
                Log.d("peckapp", obj.toString());
            }
        });

        JsonConverter<Event> eventConverter = new JsonConverter<Event>();

        ContentValues cv = eventConverter.tToContentValues(event);

        Circle circle = new Circle();
        circle.getUsers().add(5);
        circle.getUsers().add(3);

        Log.d("", "");

        JsonConverter<Circle> cDJC = new JsonConverter<Circle>();
        ContentValues contentValues = cDJC.tToContentValues(circle);

        DataSource<Circle> dataSource = new DataSource<Circle>(CirclesDataSpec.getInstance());



        Log.d("peckapp", "");
    }


    public static class Constants {

        public final static class Network {
            public final static String API_STRING = "http://thor.peckapp.com:3500/api/";
            public final static String EVENTS = "simple_events/";
            public final static String CIRCLES = "circles/";
            public final static String MEAL = "";



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
