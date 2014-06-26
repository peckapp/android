package com.peck.android.network.NetworkSpec;

import com.peck.android.PeckApp;
import com.peck.android.models.Event;

import java.lang.reflect.Type;

/**
 * Created by mammothbane on 6/26/2014.
 */
public class EventSpec implements NetworkSpec<Event> {

    @Override
    public String getApiExtension() {
        return PeckApp.Constants.Network.EVENTS;
    }

    @Override
    public Type getType() {
        return Event.class;
    }
}
