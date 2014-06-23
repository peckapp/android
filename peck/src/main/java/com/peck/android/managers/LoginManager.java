package com.peck.android.managers;

import android.util.Log;

import com.peck.android.activities.LoginActivity;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;

/**
 * Created by mammothbane on 5/22/2014.
 */
public class LoginManager implements Singleton {
    private ButtonManager buttonManager;
    private LoginActivity activity;
    private static LoginManager loginManager = new LoginManager();
    private static final String tag = "LoginManager";

    public static LoginManager getLoginManager() {
        return loginManager;
    }

    private LoginManager() {

    }

    public static void login(String user, String pass, Callback<Boolean> callback) {
        Log.d(tag, "logged in!"); //TODO: implement login
        callback.callBack(true);
    }

    public static void createAccount(String user, String pass, Callback<Boolean> callback) {
        Log.d(tag, "account created!");
        callback.callBack(true);
    }

}
