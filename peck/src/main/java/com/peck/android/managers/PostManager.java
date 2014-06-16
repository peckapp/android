package com.peck.android.managers;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class PostManager extends Manager<Event> implements Singleton {


    private static PostManager manager = new PostManager();

    private PostManager() {

    }

    public static PostManager getManager() {
        return manager;
    }




}
