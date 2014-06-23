package com.peck.android.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.facebook.model.GraphUser;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.database.helper.UserOpenHelper;
import com.peck.android.database.source.DataSource;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.User;

import java.io.FileOutputStream;

/**
 * Created by mammothbane on 6/19/2014.
 */
public class PeckSessionManager extends Manager implements Singleton {
    //manages user state, pulling from facebook if connected

    private final static String PROFILE_FILENAME = "proPicCache";
    private final static String TAG = "UserSessionManager";

    private static PeckSessionManager manager = new PeckSessionManager();
    private static int profileDimens;
    private static Context context;

    private static User user = new User();
    private static DataSource<User> dataSource;

    private static boolean facebookMode = false;
    private static boolean peckAuth = false;
    private static SourcePref sourcePref = SourcePref.FACEBOOK;



    static {
        dataSource = new DataSource<User>(UserOpenHelper.getHelper());
        context = PeckApp.AppContext.getContext();
        profileDimens = context.getResources().getDimensionPixelSize(R.dimen.prof_picture_bound);
    }

    public enum SourcePref { FACEBOOK, PECK }

    private PeckSessionManager() {}

    public static PeckSessionManager getManager() {
        return manager;
    }

    public static void init() {
        Log.i(TAG, "initializing");

        FacebookSessionManager.init();

        //test: remove before production
        context.deleteDatabase(PeckApp.Constants.Database.DATABASE_NAME); //TEST: remove before production
        SharedPreferences.Editor edit = context.getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).edit();
        edit.clear();
        edit.commit();
        Log.i(TAG, "deleted database, cleared user prefs shared preferences");

        UserManager.getManager().initialize(dataSource, new Callback() {
            @Override
            public void callBack(Object obj) {
                user.setLocalId(context.getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).getInt(PeckApp.Constants.Preferences.USER_ID, 0));
                //load saved user id from sharedpreferences

                user = UserManager.getManager().getById(user.getLocalId());

                if (user == null) { user = UserManager.getManager().add( new User()); }
                else LoginManager.authenticateUsingCached(new Callback<Boolean>() {
                    @Override
                    public void callBack(Boolean obj) {

                    }
                });

                //todo: contact loginmanager, try to use cached credentials to authenticate with peck servers

                ImageCacher.init(user.getServerId());

                Log.i(TAG, "initialized with user " + user.getServerId());
            }

        });



    }

    public static void notifyFbStateChanged(final boolean loggedIn) {
        facebookMode = loggedIn;
        FacebookSessionManager.getGraphUser(new Callback<GraphUser>() {
            @Override
            public void callBack(GraphUser obj) {
                user.setFbId(obj.getId());
                if (!peckAuth && loggedIn) user.setName(obj.getName());
            }
        });
    }

    public static User getUser() {
        return user;
    }

    public static void setSourcePref(SourcePref pref) {
        sourcePref = pref;
    }


    protected static void getImage(int userId, Callback<Bitmap> callback) {
        getImage(userId, profileDimens, callback);
    }

    protected static void getImage(final int userId, int dimens, final Callback<Bitmap> callback) {
        String URL = "";

            if (facebookMode && sourcePref == SourcePref.FACEBOOK) {
                URL = "https://graph.facebook.com/" + UserManager.getManager().getById(userId).getFbId() +
                        "/picture?width=" + dimens + "&height=" + dimens;

            } else if (peckAuth && sourcePref == SourcePref.PECK) {

            } else {
            }

        PeckApp.getRequestQueue().add(new ImageRequest(URL, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                callback.callBack(bitmap);
            }
        }, dimens, dimens, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, "Request response " + volleyError.networkResponse.statusCode
                + ". Couldn't get image for " + userId, Toast.LENGTH_LONG).show();
                callback.callBack(null);
            }
        }));


    }

    private static Bitmap scale(int size, Bitmap bmp) {
        if (size == profileDimens) return bmp;
        else return Bitmap.createScaledBitmap(bmp, size, size, false);
    }


    private static void saveImage(String filepath, Bitmap bmp) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filepath);
            bmp.compress(Bitmap.CompressFormat.PNG, PeckApp.Constants.Graphics.PNG_COMPRESSION, outputStream);
        } catch (Exception e) {
            Log.e(manager.getClass().getName(), e.toString());
        } finally {
            try {
                outputStream.close();
            } catch (Throwable ignore) {
            }
        }
    }

}
