package com.peck.android.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
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
    private static String userName;
    private static Bitmap profilePicture;
    private static URI unix;
    private static boolean facebookMode = false;
    private static PicturePref picturePref = PicturePref.PECK;

    public static class NotDownloadedException extends Exception {
        public String failedResource;

        public NotDownloadedException(String message, String failedResource) {
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
        File file = new File(PROFILE_FILENAME);
        if (file.exists()) {
            Log.i(TAG, "loading from file saved to disk");
            FileInputStream in = null;
            try {
                in = new FileInputStream(PROFILE_FILENAME);
                profilePicture = BitmapFactory.decodeFile(PROFILE_FILENAME);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                setProfileDefault();
            } finally {
                try {
                    in.close();
                } catch (Throwable ignore) {
                }
            }
        } else {
            Log.i(TAG, "loading default");
            setProfileDefault();
        }
        Log.i(TAG, "initialized");
        FacebookSessionManager.init();
    }

    public static PeckSessionManager getManager() {
        return manager;
    }

    public static void setProfileDefault() {
        profilePicture = BitmapFactory.decodeResource(context.getResources(), PeckApp.Constants.Graphics.FILLER);
    }

    public static void setPicturePref(PicturePref pref) {
        picturePref = pref;
    }

    protected static void setFacebookMode(boolean bool) {
        facebookMode = bool;
    }

    private static Bitmap getProfilePicture(int size, PicturePref pref) {
        Bitmap ret;
        switch (pref) {

            case FACEBOOK:
                ret = scale(size, FacebookSessionManager.getFbProfilePicture());
                break;

            case PECK:
                ret = scale(size, profilePicture);
                break;

            default:
                ret = scale(size, profilePicture);
                break;

        }
        return ret;
    }

    private static Bitmap scale(int size, Bitmap bmp) {
        if (size == profileDimens) return bmp;
        else return Bitmap.createScaledBitmap(bmp, size, size, false);
    }

    public static Bitmap getProfilePicture(int size) {
        return getProfilePicture(size, picturePref);
    }

    public static Bitmap getProfilePicture() {
        return getProfilePicture(profileDimens);
    }


    protected static void updateProfilePicture(final URL source, final int pixelDimens,
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
                        handleNDE(new NotDownloadedException(TAG, resourceName));
                    } else if (profileDimens == pixelDimens) {
                        callback.callBack(bmp);
                    }
                    callback.callBack(bmp);
                }
            }.execute();

    }


    private static void saveProfilePicture() {
        saveProfilePicture(PROFILE_FILENAME, profilePicture);
        saveProfilePicture(FacebookSessionManager.PROFILE_FILENAME, getProfilePicture(profileDimens, PicturePref.FACEBOOK));
    }

    private static void saveProfilePicture(String filepath, Bitmap bmp) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filepath);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
        } catch (Exception e) {
            Log.e(manager.getClass().getName(), e.toString());
        } finally {
            try {
                outputStream.close();
            } catch (Throwable ignore) {}
        }
    }

    //going to have to pull/push changes to/from server

    protected static void handleNDE(NotDownloadedException nde) {
        Log.e(TAG, nde.toString());
        Toast.makeText(context, "Couldn't download your " + nde.getFailedResource(), Toast.LENGTH_SHORT).show();
    }

}
