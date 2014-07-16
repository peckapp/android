package com.peck.android;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;
import com.peck.android.database.DBUtils;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.PeckSessionHandler;
import com.peck.android.models.Circle;
import com.peck.android.models.Comment;
import com.peck.android.models.Event;
import com.peck.android.models.Locale;
import com.peck.android.models.Peck;
import com.peck.android.models.User;
import com.squareup.picasso.Picasso;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.HashMap;

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


    private static final HashMap<Class, String> API_STRINGS = new HashMap<Class, String>();

    static {
        API_STRINGS.put(Circle.class, "circles");
        API_STRINGS.put(Event.class, "simple_events");
        API_STRINGS.put(Locale.class, "institutions");
        API_STRINGS.put(Peck.class, "push_notifications");
        API_STRINGS.put(Comment.class, "comments");
        API_STRINGS.put(User.class, "users");
    }

    public static Class[] getModelArray() {
        return API_STRINGS.keySet().toArray(new Class[API_STRINGS.entrySet().size()]);
    }

    public static Uri buildLocalUri(Class tClass) {
        if (!API_STRINGS.containsKey(tClass)) throw new IllegalArgumentException("You must pass a model class to this method");
        return Constants.Database.BASE_AUTHORITY_URI.buildUpon().appendPath(DBUtils.getTableName(tClass)).build();
    }

    public static String buildEndpointURL(Class tClass) {
        if (!API_STRINGS.containsKey(tClass)) throw new IllegalArgumentException("You must pass a model class to this method");
        return Constants.Network.ENDPOINT + API_STRINGS.get(tClass) + "/";
    }

    public static String getJsonHeader(Class tClass, boolean plural) {
        if (!API_STRINGS.containsKey(tClass)) throw new IllegalArgumentException("You must pass a model class to this method");
        if (plural) return API_STRINGS.get(tClass);
        else return (API_STRINGS.get(tClass).substring(0, API_STRINGS.get(tClass).length() - 1));
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

        if (BuildConfig.DEBUG) {
            Picasso.with(getContext()).setIndicatorsEnabled(true);
            Picasso.with(getContext()).setLoggingEnabled(true);

            /*synchronized (DatabaseManager.class) {
                DatabaseManager.closeDB();
                getContext().deleteDatabase(PeckApp.Constants.Database.DATABASE_NAME);
            }
*/
            SharedPreferences.Editor edit = getContext().getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).edit();
            edit.clear();
            edit.apply();
            Log.d("PeckApp", "cleared USER_PREFS SharedPreferences");
        }

        PeckSessionHandler.init();

    }



    public static class Constants {

        public final static class Network {

            public final static int RETRY_INTERVAL = 200;
            public final static int CONNECT_TIMEOUT = 5000;
            public final static int READ_TIMEOUT = 6000;

            /**
             * API strings
             */
            public final static String ENDPOINT = "http://thor.peckapp.com:3500/api/";


            public final static String API_TEST_KEY = "";


            public final static String INSTITUTION = "institution_id";

        }


        public final static class Preferences {
            public final static String USER_PREFS = "user preferences";
            public final static String USER_ID = "persistent user id";

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


    private static RequestQueue requestQueue;

    public static RequestQueue getRequestQueue() {
        if (requestQueue == null) requestQueue = Volley.newRequestQueue(getContext(),
                new HttpClientStack(HttpClients.custom().setConnectionManager(new PoolingHttpClientConnectionManager()).build()));
        return requestQueue;
    }
}
