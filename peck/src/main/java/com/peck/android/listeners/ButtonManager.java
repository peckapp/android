package com.peck.android.listeners;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.peck.android.activities.LoginActivity;

/**
 * Created by mammothbane on 5/21/2014.
 */
public class ButtonManager {
    private static String tag = "ButtonManager";
    private static long retry = 10L;

    public static void listenAsync(final int id, final View.OnClickListener listener, final long interval, final Activity activity){
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

    public static void listenerAsync(final int id, final View.OnClickListener listener, final Activity activity) {
        listenAsync(id, listener, retry, activity);
    }

    public static void start(int id, LoginActivity activity) {

    }

}
