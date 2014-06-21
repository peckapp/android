package com.peck.android.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.database.helper.UserOpenHelper;
import com.peck.android.database.source.DataSource;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.User;

import java.io.FileOutputStream;
import java.net.URL;

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

    private static User user;
    private static int userId;
    private static DataSource<User> dataSource;

    private static Bitmap profilePicture;
    private static boolean facebookMode = false;
    private static boolean peckAuth;
    private static PicturePref picturePref = PicturePref.PECK;

    static {
        dataSource = new DataSource<User>(UserOpenHelper.getHelper());

    }

    public static User getUser() {
        if (user == null) Log.e(TAG, "user is null");
        return user;
    }


    public static class NotAvailableException extends Exception {
        public String failedResource;

        public NotAvailableException(String message, String failedResource) {
            super(message);
            this.failedResource = failedResource;
        }

        public String getFailedResource() {
            return failedResource;
        }
    }

    public enum PicturePref { FACEBOOK, PECK }

    static {
        context = PeckApp.AppContext.getContext();
        profileDimens = context.getResources().getDimensionPixelSize(R.dimen.prof_picture_bound);
    }

    private PeckSessionManager() {}

    public static void init() {
        Log.i(TAG, "initializing");

        UserManager.getManager().initialize(dataSource, new Callback() {
            @Override
            public void callBack(Object obj) {
                userId = context.getSharedPreferences(PeckApp.Constants.Preferences.USER_PREFS, Context.MODE_PRIVATE).getInt(PeckApp.Constants.Preferences.USER_ID, 0);
                //load saved user id from sharedpreferences
                if (userId == 0) {
                    peckAuth = false;
                    user = dataSource.create(new User());
                    userId = user.getLocalId();
                } else {
                    peckAuth = true;
                    user = UserManager.getManager().getById(userId);
                }

                FacebookSessionManager.init();

                ImageCacher.init(PROFILE_FILENAME, userId);

                Log.i(TAG, "initialized with user " + user.getLocalId());
            }

        });



    }

    public static PeckSessionManager getManager() {
        return manager;
    }

    public static void setPicturePref(PicturePref pref) {
        picturePref = pref;
    }




    protected static Bitmap getImage(int userId) throws NotAvailableException {
        return getImage(userId, profileDimens);
    }

    protected static Bitmap getImage(int userId, int dimens) throws NotAvailableException {

        return null; //this should be an api call to peck servers

    }


    protected static void setFacebookMode(boolean bool) {
        facebookMode = bool;
    }

    private static Bitmap scale(int size, Bitmap bmp) {
        if (size == profileDimens) return bmp;
        else return Bitmap.createScaledBitmap(bmp, size, size, false);
    }

    protected static int getUserId() {
        return userId;
    }





    protected static void getImageFromURL(final URL source, final int pixelDimens,
                                          final Callback<Bitmap> callback, final String resourceName) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... strings) {
                try {
                    return BitmapFactory.decodeStream(source.openConnection().getInputStream());
                } catch (Exception e) {
                    Log.e(getClass().getName(), e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bmp) {
                if (bmp == null) {
                    handleNDE(new NotAvailableException(TAG, resourceName));
                } else if (profileDimens == pixelDimens) {
                    callback.callBack(bmp);
                }
                callback.callBack(bmp);
            }
        }.execute();

    }


    private static void saveProfilePicture() {
        saveImage(PROFILE_FILENAME, ImageCacher.get(userId));
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
            } catch (Throwable ignore) {}
        }
    }

    //going to have to pull/push changes to/from server

    protected static void handleNDE(NotAvailableException nde) {
        Log.e(TAG, nde.toString());
        Toast.makeText(context, "Couldn't download your " + nde.getFailedResource(), Toast.LENGTH_SHORT).show();
    }

}
