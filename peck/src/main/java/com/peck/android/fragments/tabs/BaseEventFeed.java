package com.peck.android.fragments.tabs;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.DataSource;
import com.peck.android.database.dataspec.EventDataSpec;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/10/2014.
 */

public abstract class BaseEventFeed extends FeedTab<Event> {

    public BaseEventFeed setUpFeed() {

        if (dataSource == null) {
            dataSource = new DataSource<Event>(EventDataSpec.getHelper());
        }

        if (feedAdapter == null) {
            feedAdapter = new FeedAdapter<Event>(dataSource.generate().getResourceId());
        }

        return this;
    }

}