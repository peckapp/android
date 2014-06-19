package com.peck.android.managers;

import com.peck.android.interfaces.Singleton;

/**
 * Created by mammothbane on 6/19/2014.
 */
public class FacebookSessionManager extends Manager implements Singleton {
    private static FacebookSessionManager manager = new FacebookSessionManager();

    private FacebookSessionManager() {

    }

    public FacebookSessionManager getManager() {
        return manager;
    }



}
