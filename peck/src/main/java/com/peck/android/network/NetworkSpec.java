package com.peck.android.network;

import com.peck.android.PeckApp;
import com.peck.android.models.Circle;
import com.peck.android.models.Event;

import java.lang.reflect.Type;

/**
 * Created by mammothbane on 6/26/2014.
 */
public interface NetworkSpec<T> {

    public String getApiExtension();

    public Type getType();

    public static class EventSpec implements NetworkSpec<Event> {
        @Override
        public String getApiExtension() {
            return PeckApp.Constants.Network.EVENTS;
        }

        @Override
        public Type getType() {
            return Event.class;
        }
    }

    public static class CircleSpec implements NetworkSpec<Circle> {
        @Override
        public String getApiExtension() {
            return PeckApp.Constants.Network.CIRCLES;
        }

        @Override
        public Type getType() {
            return Circle.class;
        }

    }



}
