package com.peck.android.factories;

import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/9/2014.
 */
public class EventFactory extends GenericFactory<Event> {

    private static EventFactory eventFactory = new EventFactory();

    public Event generate() {
        return new Event();
    }

    private EventFactory() {

    }

    public EventFactory getFactory() {
        return eventFactory;
    }

}
