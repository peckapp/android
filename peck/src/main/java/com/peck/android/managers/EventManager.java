package com.peck.android.managers;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.DataSource;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Event;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/11/2014.
 */
public class EventManager extends FeedManager<Event> implements Singleton {

    private static EventManager manager = new EventManager();

    private EventManager() {

    }

    @Override
    public FeedManager<Event> initialize(FeedAdapter<Event> adapter, DataSource<Event> dSource) {
        super.initialize(adapter, dSource);
        ArrayList<Event> events =  new ArrayList<Event>();

        Event e;
        for (int i = 1; i < 21; i++) {
            e = new Event();
            e.setServerId(i);
            e.setLocalId(i);
            e.setTitle("Event " + Integer.toString(i));
            e.setText("Text " + Integer.toString(i));
            events.add(e);
        }
        add(events);
        return this;
    }

    public static EventManager getManager() {
        return manager;
    }


}
