package com.peck.android.activities;

import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.peck.android.R;
import com.peck.android.fragments.AccountFragment;
import com.peck.android.fragments.LoginFragment;
import com.peck.android.managers.LoginManager;


public class LoginActivity extends FragmentActivity {

    private static final String TAG = "LoginActivity";
    private boolean inDefaultState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        android.support.v4.app.FragmentTransaction transact = getSupportFragmentManager().beginTransaction(); //put the login button fragment

        transact.add(R.id.ll_bt_login, new LoginFragment());
        transact.commit();
        inDefaultState = true;

        LoginManager.getLoginManager().initialize(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setLoginState() { //set the login state
        getSupportFragmentManager().popBackStack();
        inDefaultState = true;
    }

    public void setCreateState() { //set the account creation state
        android.support.v4.app.FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.ll_bt_login, new AccountFragment());
        trans.addToBackStack(null);
        trans.commit();

        inDefaultState = false;
    }

    public boolean isInDefaultState() { return inDefaultState; }

}


