package com.peck.android.factories;

import com.peck.android.models.Event;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class EventFactory extends GenericFactory<Event> {

    public Event getNew() {
        return new Event();
    }
}
