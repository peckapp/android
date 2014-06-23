package com.peck.android.managers;

import android.content.Context;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.peck.android.PeckApp;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;

/**
 * Created by mammothbane on 6/19/2014.
 */
public class FacebookSessionManager extends Manager implements Singleton {

    private final static String TAG = "FacebookSessionManager";

    private static Session session;
    private static Context context;
    private static GraphUser user;

    private static FacebookSessionManager manager = new FacebookSessionManager();

    static {
        context = PeckApp.AppContext.getContext();
    }

    private FacebookSessionManager() {
    }

    public FacebookSessionManager getManager() {
        return manager;
    }

    public static void init() {
        Log.i(TAG, "initializing");
        session = Session.openActiveSessionFromCache(context);


        Log.i(TAG, ("initialized " + ((session == null) ? "without facebook" : "with facebook")));

    }

    public static void getGraphUser(final Callback<GraphUser> callback) {
        Request.newMeRequest(Session.getActiveSession(),
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(final GraphUser user, Response response) {
                        callback.callBack(user);
                        FacebookSessionManager.user = user;
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
                Log.i(((Object) this).getClass().getName(), "Logged in...");
                FacebookSessionManager.session = session;

                PeckSessionManager.notifyFbStateChanged(true);

            } else if (state.isClosed()) {
                Log.i(((Object)this).getClass().getName(), "Logged out...");
                FacebookSessionManager.session = null;

                PeckSessionManager.notifyFbStateChanged(false);

            }

            callback.callBack(null);

        }

    }



}
