package com.peck.android.adapters;

import android.content.Context;

import com.peck.android.factories.EventFactory;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class EventFeedAdapter extends FeedAdapter<Event> {

    public EventFeedAdapter(Context context, EventFactory factory) {
        super(context, factory);
    }
}
