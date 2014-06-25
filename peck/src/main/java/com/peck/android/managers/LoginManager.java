package com.peck.android.managers;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.peck.android.PeckApp;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;

/**
 * Created by mammothbane on 5/22/2014.
 */
public class LoginManager implements Singleton {
    private static LoginManager loginManager = new LoginManager();
    private static final String tag = "LoginManager";
    private static boolean loginstate = true;

    public static LoginManager getLoginManager() {
        return loginManager;
    }

    private LoginManager() {

    }

    public static void login(String user, String pass, Callback<Boolean> callback) {
        Log.d(tag, "logged in to peck");
        //TODO: implement login
        //meta: password should probably also be hashed
        callback.callBack(true);
        loginstate = true;
    }

    static void authenticateUsingCached(final Callback callback) {

        RequestQueue requestQueue = PeckApp.getRequestQueue();

        //todo: set up volley request
    }

    public static void createAccount(String user, String pass, Callback<Boolean> callback) {
        Log.d(tag, "account created for peck");
        callback.callBack(true);
    }

    public static boolean isLoggedIn() {
        return loginstate; //todo: work on this; perhaps an async check with the server
    }

    public static void logout() {
        //meta: what do we do here?
        loginstate = false;
    }


}
