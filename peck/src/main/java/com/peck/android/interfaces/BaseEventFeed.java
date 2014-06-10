package com.peck.android.interfaces;

import com.peck.android.factories.EventFactory;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/10/2014.
 */

public abstract class BaseEventFeed extends Feed<Event, EventFactory> {

    public EventFactory getFactory() {
        return new EventFactory();
    }


}