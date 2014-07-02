package com.peck.android.managers;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Peck;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class PeckManager extends FeedManager<Peck> implements Singleton {

    //todo: override everything

    private static PeckManager manager = new PeckManager();

    private PeckManager() {

    }

    public static PeckManager getManager() {
        return manager;
    }




}
