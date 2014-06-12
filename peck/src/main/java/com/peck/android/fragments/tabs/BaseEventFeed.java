package com.peck.android.fragments.tabs;

import com.peck.android.adapters.EventFeedAdapter;
import com.peck.android.database.source.EventDataSource;
import com.peck.android.factories.EventFactory;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/10/2014.
 */

public abstract class BaseEventFeed extends Feed<Event> {

    public EventFactory getFactory() {
        return new EventFactory();
    }

    protected BaseEventFeed setUpAdapter() {
        if (feedAdapter == null) {
            feedAdapter = new EventFeedAdapter(getActivity(), getFactory());
        }

        if (dataSource == null) {
            dataSource = new EventDataSource(getActivity());
        }

        return this;
    }

}