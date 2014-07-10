package com.peck.android.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.facebook.model.GraphUser;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.User;
import com.squareup.otto.Subscribe;

import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by mammothbane on 6/19/2014.
 *
 * handles the user's persistent session
 *
 */
public class PeckSessionHandler implements Singleton {
    //manages user state, pulling from facebook if connected

    private final static String PROFILE_FILENAME = "proPicCache";
    private final static String TAG = "UserSessionManager";

    private static PeckSessionHandler handler = new PeckSessionHandler();
    private static int profileDimens;
    private static Context context;

    private static User user = new User();
    private static Date sessionStart;

    private static boolean facebookMode = false;
    private static boolean peckAuth = false;
    private static SourcePref sourcePref = SourcePref.FACEBOOK;



    static {
        context = PeckApp.getContext();
        profileDimens = context.getResources().getDimensionPixelSize(R.dimen.prof_picture_bound);
    }

    public enum SourcePref { FACEBOOK, PECK }

    private PeckSessionHandler() {}

    public static PeckSessionHandler getHandler() {
        return handler;
    }

    public static void init() {
        Log.i(TAG, "initializing");

        sessionStart = new Date(System.currentTimeMillis());

        FacebookSessionHandler.init();

        DataHandler.register(User.class, getHandler());
        DataHandler.init(User.class);

    }

    @Subscribe
    public void respondToInit(DataHandler.InitComplete complete) {
        User user = DataHandler.getByLocalId(User.class, context.getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).getInt(PeckApp.Constants.Preferences.USER_ID, 0));

        if (user == null) {
            user = new User();
            DataHandler.put(User.class, user, true); //temp user
            //todo: server auth
        }
        else {
            final User ret = user;
            LoginManager.authenticateUsingCached(new Callback<Boolean>() {
                @Override
                public void callBack(Boolean obj) {
                    if (obj) {
                        Log.i(TAG, "initialized with user " + ret.getServerId());
                    } else {
                        //todo: give the user an alert dialog, prompting them to log in

                    }
                }
            });
        }
    }



    public static void notifyFbStateChanged(final boolean loggedIn) {
        facebookMode = loggedIn;
        FacebookSessionHandler.getGraphUser(new Callback<GraphUser>() {
            @Override
            public void callBack(GraphUser obj) {
                user.setFbId(obj.getId());
                if (!peckAuth && loggedIn) user.setFullName(obj.getName());
            }
        });
    }

    public static User getUser() {
        return user;
    }

    public static Date getSessionStart() {
        return sessionStart;
    }

    public static void setSourcePref(SourcePref pref) {
        sourcePref = pref;
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
            Log.e(handler.getClass().getName(), e.toString());
        } finally {
            try {
                outputStream.close();
            } catch (Throwable ignore) {
            }
        }
    }

}
