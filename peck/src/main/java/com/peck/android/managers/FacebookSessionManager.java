package com.peck.android.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mammothbane on 6/19/2014.
 */
public class FacebookSessionManager extends Manager implements Singleton {
    protected final static String PROFILE_FILENAME = "fbProPicCache";

    private final static int profileDimens;
    private final static String TAG = "FacebookSessionManager";

    private static Session session;
    private static String userName;
    private static Context context;
    private static String token;
    private static String profId;
    private static Bitmap fbProfilePicture;

    private static boolean shouldUpdate = true;

    private static FacebookSessionManager manager = new FacebookSessionManager();

    static {
        context = PeckApp.AppContext.getContext();
        profileDimens = context.getResources().getDimensionPixelSize(R.dimen.prof_picture_bound);
    }

    private FacebookSessionManager() {
    }

    public FacebookSessionManager getManager() {
        return manager;
    }

    public static void init() {
        Log.i(TAG, "initializing");
        session = Session.openActiveSessionFromCache(context);

        if (session == null) PeckSessionManager.setFacebookMode(false);
        else {
            PeckSessionManager.setFacebookMode(true);
            if (Session.getActiveSession().isOpened()) {

                updateUserDetails(new Callback() {
                    @Override
                    public void callBack(Object obj) {
                    }
                });

            }
        }
        Log.i(TAG, ("initialized " + ((session == null) ? "without facebook" : "with facebook")));

    }

    public static void updateUserDetails(final Callback callback) {
        updateGraphUser();

        updateFbProfilePicture(new Callback<Bitmap>() {
            @Override
            public void callBack(Bitmap obj) {
                fbProfilePicture = obj;
            }
        });
        callback.callBack(null);

    }

    protected static void updateFbProfilePicture(Callback<Bitmap> callback) {
        updateFbProfilePicture(profileDimens, callback);
    }

    protected static void updateFbProfilePicture(final int pixelDimens, final Callback<Bitmap> callback) {
        URL source = null;
        try {
            source = new URL("https://graph.facebook.com/" + profId + "/picture?width=" + pixelDimens + "&height=" + pixelDimens);
        } catch (MalformedURLException m) {
            Log.e(TAG, m.toString());
        }

        PeckSessionManager.getImageFromURL(source, pixelDimens,
                new Callback<Bitmap>() {
                    @Override
                    public void callBack(Bitmap bmp) {
                        fbProfilePicture = bmp;
                        callback.callBack(bmp);
                    }
                }, "Facebook profile picture"
        );
    }


    public static String getUserName() {
        return userName;
    }

    public static void updateGraphUser() {
        Request.newMeRequest(Session.getActiveSession(),
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(final GraphUser user, Response response) {
                        profId = user.getId();
                        userName = user.getName();
                    }
                }
        ).executeAsync();
    }

    public static class SessionStatusCallback implements Session.StatusCallback {
        Callback callback;

        public SessionStatusCallback(Callback callback) {
            this.callback = callback;
        }

        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }


        private void onSessionStateChange(Session session, SessionState state, Exception exception) {
            if (state.isOpened()) {
                Log.i(((Object)this).getClass().getName(), "Logged in...");
                FacebookSessionManager.session = session;
                FacebookSessionManager.updateUserDetails(callback);

            } else if (state.isClosed()) {
                Log.i(((Object)this).getClass().getName(), "Logged out...");
                FacebookSessionManager.session = null;

                //TODO: revert non-peck facebook information

            }
        }

    }



}
