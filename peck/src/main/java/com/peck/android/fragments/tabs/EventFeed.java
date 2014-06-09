package com.peck.android.fragments.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peck.android.R;
import com.peck.android.factories.EventFactory;
import com.peck.android.interfaces.Feed;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/9/2014.
 */

public class EventFeed extends Feed<Event, EventFactory> {

    private final static String tag = "Events";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_events, container, false);
    }

    public String getTabTag() {
        return tag;
    }
}
