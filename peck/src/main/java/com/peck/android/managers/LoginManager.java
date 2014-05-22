package com.peck.android.managers;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.peck.android.R;
import com.peck.android.activities.LoginActivity;
import com.peck.android.fragments.AccountFragment;

import java.util.ArrayList;

/**
 * Created by mammothbane on 5/22/2014.
 */
public class LoginManager {
    private boolean inDefaultState;
    private ButtonManager buttonManager;
    private LoginActivity activity;
    private static LoginManager loginManager = new LoginManager();
    private static final String tag = "LoginManager";
    private boolean initialized = false;

    private final int[] initIds = {R.id.bt_login, R.id.bt_acct_prompt};
    private ArrayList<View.OnClickListener> initListeners;

    private final int[] otherIds = {R.id.bt_create_acct, R.id.bt_cancel};
    private ArrayList<View.OnClickListener> otherListeners;

    public static LoginManager getLoginManager() {
        return loginManager;
    }

    private LoginManager() {

    }

    public void initialize(LoginActivity activity) {
        this.activity = activity;

        initListeners = new ArrayList<View.OnClickListener>();
        initListeners.add(new LoginListener());
        initListeners.add(new ToggleListener());

        otherListeners = new ArrayList<View.OnClickListener>();
        otherListeners.add(new AccountListener());
        otherListeners.add(new ToggleListener());

        buttonManager = new ButtonManager(activity);
        try {inDefaultState = buttonManager.setListeners(initIds, initListeners);} catch (Exception e) {e.printStackTrace();}

        initialized = true;
    }

    void login() {
        activity.findViewById(R.id.sp_login).setVisibility(View.VISIBLE);
        Log.d(tag, "logged in!"); //TODO: implement login
    }

    void createAccount() {
        activity.findViewById(R.id.sp_acct).setVisibility(View.VISIBLE);
        Log.d(tag, "account created!"); //TODO: implement account creation
    }



    public void toggle() {
        //TODO: cancel/ignore new server communication if user toggles fragment state

        if (activity.isInDefaultState()) {
            activity.setCreateState();
            try {
                buttonManager.setListeners(otherIds, otherListeners);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            activity.setLoginState();
            try {
                buttonManager.setListeners(initIds, initListeners);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }



    }

}


class LoginListener implements View.OnClickListener {

    private static final String tag = "LoginListener";

    @Override
    public void onClick(View view) {
        //TODO: manage login
        Log.d(tag, "this is where you log in");
        LoginManager.getLoginManager().login();
    }

}

class AccountListener implements View.OnClickListener {

    private static final String tag = "AccountListener";

    public void onClick(View view) {
        //TODO: manage account creation
        Log.d(tag, "account creation happens here");
        LoginManager.getLoginManager().createAccount();
    }
}

class ToggleListener implements View.OnClickListener {
    private final static String tag = "ToggleListener";


    @Override
    public void onClick(View view) {
        LoginManager.getLoginManager().toggle();
    }

}
