package com.peck.android.listeners;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.peck.android.R;
import com.peck.android.fragments.AccountFragment;

/**
 * Created by mammothbane on 5/21/2014.
 */
public class AccountListener implements View.OnClickListener {
    private final static String tag = "AccountListener";
    Activity activity = null;

    public AccountListener(Activity activity) {
        this.activity = activity;
        //ButtonManager.listenerAsync(R.id.bt_acct_prompt, this, activity);
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction trans = activity.getFragmentManager().beginTransaction();
        trans.replace(R.id.ll_bt_login, new AccountFragment());
        trans.addToBackStack(null);
        trans.commit();

        ButtonManager.listenerAsync(R.id.bt_create_acct, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: implement account creation
                Log.d(tag, "account created!");
            }
        }, activity);

        ButtonManager.listenerAsync(R.id.bt_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getFragmentManager().popBackStack();
                new AccountListener(activity);
                new LoginListener();
                //ButtonManager.listenAsync(R.id.bt_acct_prompt, new AccountListener(activity), activity);
                //ButtonManager.listenAsync(R.id.bt_login, new AccountListener(activity), activity);
            }
        }, activity);

    }

}
