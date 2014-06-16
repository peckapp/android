package com.peck.android.managers;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/11/2014.
 */
public class EventManager extends FeedManager<Event> implements Singleton {

    private static EventManager manager = new EventManager();

    private EventManager() {

    }

    public static EventManager getManager() {
        return manager;
    }


}
