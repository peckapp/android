package com.peck.android.fragments;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.DataSource;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/10/2014.
 */

public abstract class BaseEventFeed extends FeedTab<Event> {

    public BaseEventFeed setUpFeed() {

        if (dataSource == null) {
            dataSource = new DataSource<Event>(new Event());
        }

        if (feedAdapter == null) {
            feedAdapter = new FeedAdapter<Event>(new Event().getResourceId());
        }

        return this;
    }

}