package com.peck.android.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.facebook.model.GraphUser;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.database.DataSource;
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
public class PeckSessionManager extends Manager implements Singleton {
    //manages user state, pulling from facebook if connected

    private final static String PROFILE_FILENAME = "proPicCache";
    private final static String TAG = "UserSessionManager";

    private static PeckSessionManager manager = new PeckSessionManager();
    private static int profileDimens;
    private static Context context;

    private static User user = new User();
    private static DataSource<User> dataSource;
    private static Date sessionStart;

    private static boolean facebookMode = false;
    private static boolean peckAuth = false;
    private static SourcePref sourcePref = SourcePref.FACEBOOK;



    static {
        dataSource = new DataSource<User>(new User());
        context = PeckApp.getContext();
        profileDimens = context.getResources().getDimensionPixelSize(R.dimen.prof_picture_bound);
    }

    public enum SourcePref { FACEBOOK, PECK }

    private PeckSessionManager() {}

    public static PeckSessionManager getManager() {
        return manager;
    }

    public static void init() {
        Log.i(TAG, "initializing");

        sessionStart = new Date(System.currentTimeMillis());

        FacebookSessionManager.init();

        //test: remove before production
        context.deleteDatabase(PeckApp.Constants.Database.DATABASE_NAME);
        SharedPreferences.Editor edit = context.getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).edit();
        edit.clear();
        edit.apply();
        Log.i(TAG, "deleted database, cleared USER_PREFS SharedPreferences");

        UserManager.getManager().initialize(dataSource, new Callback<ArrayList<User>>() {
            @Override
            public void callBack(ArrayList<User> obj) {
                user.setLocalId(context.getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).getInt(PeckApp.Constants.Preferences.USER_ID, 0));
                //load saved user id from sharedpreferences

                user = UserManager.getManager().getByLocalId(user.getLocalId());

                if (user == null) {
                    user = new User();
                    UserManager.getManager().addNetwork(user, new Callback<User>() {
                        @Override
                        public void callBack(User obj) {
                            user.setLocalId(obj.getLocalId());
                        }
                    });
                }
                else LoginManager.authenticateUsingCached(new Callback<Boolean>() {
                    @Override
                    public void callBack(Boolean obj) {
                        if (obj) {
                            ImageCacher.init(user);
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
        FacebookSessionManager.getGraphUser(new Callback<GraphUser>() {
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
