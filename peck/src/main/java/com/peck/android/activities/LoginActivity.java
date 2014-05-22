package com.peck.android.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.peck.android.R;
import com.peck.android.fragments.AccountFragment;
import com.peck.android.fragments.LoginFragment;
import com.peck.android.intefaces.ToggleableActivity;
import com.peck.android.listeners.ButtonManager;

import java.util.ArrayList;


public class LoginActivity extends ActionBarActivity implements ToggleableActivity {

    public static final String tag = "LoginActivity";
    private boolean inDefaultState;
    private ButtonManager buttonManager;

    private final int[] initIds = {R.id.bt_login, R.id.bt_acct_prompt};
    private ArrayList<View.OnClickListener> initListeners;

    private final int[] otherIds = {R.id.bt_create_acct, R.id.bt_cancel};
    private ArrayList<View.OnClickListener> otherListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentTransaction transact = getFragmentManager().beginTransaction();
        transact.add(R.id.ll_bt_login, new LoginFragment());
        transact.commit();

        initListeners = new ArrayList<View.OnClickListener>();
        initListeners.add(new LoginListener());
        initListeners.add(new ToggleListener(this));

        otherListeners = new ArrayList<View.OnClickListener>();
        otherListeners.add(new AccountListener());
        otherListeners.add(new ToggleListener(this));

        buttonManager = new ButtonManager(this);
        try {inDefaultState = buttonManager.setListeners(initIds, initListeners);} catch (Exception e) {e.printStackTrace();}

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

    public void toggle() {
        if (inDefaultState) {
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            trans.replace(R.id.ll_bt_login, new AccountFragment());
            trans.addToBackStack(null);
            trans.commit();

            try {buttonManager.setListeners(otherIds, otherListeners);} catch (Exception e) {e.printStackTrace();}

            inDefaultState = false;
        } else {
            getFragmentManager().popBackStack();

            try { buttonManager.setListeners(initIds, initListeners); } catch (Exception e) {e.printStackTrace();}

            inDefaultState = true;
        }

    }
}

class LoginListener implements View.OnClickListener {

    private static final String tag = "LoginListener";

    @Override
    public void onClick(View view) {
        //TODO: manage login
        Log.d(tag, "this is where you log in");

    }

}

class AccountListener implements View.OnClickListener {

    private static final String tag = "AccountListener";

    public void onClick(View view) {
        //TODO: manage account creation
        Log.d(tag, "account creation happens here");
    }
}

class ToggleListener implements View.OnClickListener {
    private final static String tag = "ToggleListener";
    ToggleableActivity activity = null;


    public ToggleListener(ToggleableActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        activity.toggle();
    }

}

