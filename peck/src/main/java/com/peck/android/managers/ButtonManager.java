package com.peck.android.managers;

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
    private static long retry = 10L; //the default retry time of 10ms
    private Activity activity;
    private boolean state;

    public ButtonManager(Activity activity) {
        this.activity = activity;

    }

    public static void listenAsync(final int id, final View.OnClickListener listener, final long interval, final Activity activity){
        //fork a new thread to check whether the button in question has been created by the OS,
        //and hand it a click listener once it has

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

    public static void listenAsync(final int id, final View.OnClickListener listener, final Activity activity) {
        listenAsync(id, listener, retry, activity); //default to the retry time specified above
    }

    public boolean setListeners(int[] ids, ArrayList<View.OnClickListener> listeners) throws Exception {
        //call listenAsync on every id/listener pair in the parameter arrays

        if (activity == null) throw new Exception("you must give this object an activity on which to act");
        if (ids.length != listeners.size()) throw new Exception("number of buttons must be equal to number of listeners");
        for (int i = 0; i < ids.length; i++) {
            listenAsync(ids[i], listeners.get(i), activity);
        }
        return true;
    }

}