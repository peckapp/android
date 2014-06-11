package com.peck.android.managers;

import com.peck.android.database.helper.EventOpenHelper;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/11/2014.
 */
public class EventManager extends ModelManager<Event, EventOpenHelper> implements Singleton {

    private static EventManager manager = new EventManager();

    private EventManager() {

    }

    public static EventManager getManager() {
        return manager;
    }


}
