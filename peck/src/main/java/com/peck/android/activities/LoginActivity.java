package com.peck.android.activities;

import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.peck.android.R;
import com.peck.android.fragments.AccountFragment;
import com.peck.android.fragments.LoginFragment;
import com.peck.android.listeners.AccountListener;
import com.peck.android.listeners.ButtonManager;
import com.peck.android.listeners.LoginListener;


public class LoginActivity extends ActionBarActivity {

    public static final String tag = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentTransaction transact = getFragmentManager().beginTransaction();
        transact.add(R.id.ll_bt_login, new LoginFragment());
        transact.commit();

        ButtonManager.start(this);

        //new AccountListener(this);
        //new LoginListener(this);

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
}
