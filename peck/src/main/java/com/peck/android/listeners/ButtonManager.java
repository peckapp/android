package com.peck.android.listeners;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.peck.android.R;
import com.peck.android.fragments.AccountFragment;

import java.util.ArrayList;

/**
 * Created by mammothbane on 5/21/2014.
 */
public class ButtonManager {
    private static String tag = "ButtonManager";
    private static long retry = 10L;
    private Activity activity;
    private boolean state;

    public ButtonManager(Activity activity) {
        this.activity = activity;

    }

    public void listenAsync(final int id, final View.OnClickListener listener, final long interval, final Activity activity){
        new Thread() {
            public void run() {
                Button bt;
                do {
                    bt = (Button)activity.findViewById(id);
                    try {sleep(interval);} catch (InterruptedException e) { Log.d(tag, "didn't sleep"); }
                } while (bt == null);
                bt.setOnClickListener(listener);
            }
        }.start();
    }

    public void listenAsync(final int id, final View.OnClickListener listener, final Activity activity) {
        listenAsync(id, listener, retry, activity);
    }

    public boolean setListeners(int[] ids, ArrayList<View.OnClickListener> listeners) throws Exception {
        if (activity == null) throw new Exception("you must give this object an activity on which to act");
        if (ids.length != listeners.size()) throw new Exception("number of buttons must be equal to number of listeners");
        for (int i = 0; i < ids.length; i++) {
            listenAsync(ids[i], listeners.get(i), activity);
        }
        return true;
    }

}



/*ButtonManager.listenAsync(R.id.bt_create_acct, new View.OnClickListener() {
@Override
public void onClick(View view) {
        //TODO: implement account creation
        Log.d(tag, "account created!");
        }
        }, activity);

        ButtonManager.listenAsync(R.id.bt_cancel, new View.OnClickListener() {
@Override
public void onClick(View view) {
        activity.getFragmentManager().popBackStack();
        new PromptListener(activity);
        new LoginListener();
        //ButtonManager.listenAsync(R.id.bt_acct_prompt, new AccountListener(activity), activity);
        //ButtonManager.listenAsync(R.id.bt_login, new AccountListener(activity), activity);
        }
        }, activity);*/