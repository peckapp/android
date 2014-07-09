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

import java.io.FileOutputStream;
import java.util.ArrayList;
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

    private static PeckSessionHandler manager = new PeckSessionHandler();
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

    public static PeckSessionHandler getManager() {
        return manager;
    }

    public static void init() {
        Log.i(TAG, "initializing");

        sessionStart = new Date(System.currentTimeMillis());

        FacebookSessionHandler.init();



        UserManager.getManager().initialize(new Callback<ArrayList<User>>() {
            @Override
            public void callBack(ArrayList<User> obj) {
                user.setLocalId(context.getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).getInt(PeckApp.Constants.Preferences.USER_ID, 0));
                //load saved user id from sharedpreferences

                user = UserManager.getManager().getByLocalId(user.getLocalId());

                if (user == null) {
                    user = new User();
                    UserManager.getManager().getData().add(user); //temp user
                    //todo: server auth
                }
                else LoginManager.authenticateUsingCached(new Callback<Boolean>() {
                    @Override
                    public void callBack(Boolean obj) {
                        if (obj) {
                            Log.i(TAG, "initialized with user " + user.getServerId());
                        } else {
                            //todo: give the user an alert dialog, prompting them to log in

                        }
                    }
                });


            }

        });



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
            Log.e(manager.getClass().getName(), e.toString());
        } finally {
            try {
                outputStream.close();
            } catch (Throwable ignore) {
            }
        }
    }

}
