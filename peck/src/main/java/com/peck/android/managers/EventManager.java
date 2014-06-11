package com.peck.android.managers;

import com.peck.android.database.helper.EventOpenHelper;
import com.peck.android.database.source.EventDataSource;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/11/2014.
 */
public class EventManager extends ModelManager<Event, EventOpenHelper> {

    private static EventManager manager = new EventManager();

    private EventManager() {

    }

    public static EventManager getManager() {
        return manager;
    }


}
