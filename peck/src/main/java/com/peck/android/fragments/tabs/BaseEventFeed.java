package com.peck.android.fragments.tabs;

import com.peck.android.adapters.EventFeedAdapter;
import com.peck.android.database.source.EventDataSource;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/10/2014.
 */

public abstract class BaseEventFeed extends Feed<Event> {

    protected BaseEventFeed setUpAdapter() {

        if (dataSource == null) {
            dataSource = new EventDataSource(getActivity());
        }

        if (feedAdapter == null) {
            feedAdapter = new EventFeedAdapter(getActivity(), dataSource);
        }

        return this;
    }

}