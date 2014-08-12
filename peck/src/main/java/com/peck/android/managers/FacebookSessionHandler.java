/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android.managers;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.peck.android.PeckApp;
import com.peck.android.interfaces.Callback;

/**
 * Created by mammothbane on 6/19/2014.
 */
public class FacebookSessionHandler {

    private final static String TAG = "FacebookSessionManager";
    private FacebookSessionHandler() {}
    private static Session session;


    public static void init() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Log.i(TAG, "initializing");
                session = Session.openActiveSessionFromCache(PeckApp.getContext());
                Log.i(TAG, ("initialized " + ((session == null) ? "without facebook" : "with facebook")));
                return null;
            }
        }.execute();
    }

    public static void getGraphUser(final Callback<GraphUser> callback) {
        Request.newMeRequest(Session.getActiveSession(),
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(final GraphUser user, Response response) {
                        callback.callBack(user);
                    }
                }
        ).executeAsync();
    }

    public static class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (state.isOpened()) {
                Log.i(((Object) this).getClass().getName(), "logged in to facebook");
                FacebookSessionHandler.session = session;

            } else if (state.isClosed()) {
                Log.i(((Object)this).getClass().getName(), "logged out of facebook");
                FacebookSessionHandler.session = null;
            }
        }
    }
}
