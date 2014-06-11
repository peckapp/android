package com.peck.android.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.peck.android.R;
import com.peck.android.fragments.AccountFragment;
import com.peck.android.fragments.LoginFragment;
import com.peck.android.managers.LoginManager;


public class LoginActivity extends FragmentActivity {

    private static final String TAG = "LoginActivity";
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        FragmentTransaction transact = getSupportFragmentManager().beginTransaction(); //put the login button fragment

        transact.add(R.id.ll_bt_login, new LoginFragment(), LoginFragment.buildTag());
        transact.commit();

        LoginManager.getLoginManager().initialize(this);
    }

    protected void onResume() {
        super.onResume();
    }

    private void setState(int id, Fragment fragment) {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(id, fragment);
        trans.addToBackStack(null);
        trans.commit();
    }

    public void setLoginState() { //set the login state
        setState(R.id.ll_bt_login, new LoginFragment());
    }

    public void setCreateState() { //set the account creation state
        setState(R.id.ll_bt_login, new AccountFragment());
    }

    public boolean isInDefaultState() {
        Log.d(TAG, Boolean.toString(getSupportFragmentManager().findFragmentById(R.id.ll_bt_login) instanceof LoginFragment));
        return (getSupportFragmentManager().findFragmentById(R.id.ll_bt_login) instanceof LoginFragment);
    }
}


