package com.peck.android.adapters;

import android.content.Context;

import com.peck.android.interfaces.Factory;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class EventFeedAdapter extends FeedAdapter<Event> {

    public EventFeedAdapter(Context context, Factory<Event> factory) {
        super(context, factory);
    }

}
