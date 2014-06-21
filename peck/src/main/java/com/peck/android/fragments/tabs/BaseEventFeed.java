package com.peck.android.fragments.tabs;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/10/2014.
 */

public abstract class BaseEventFeed extends FeedTab<Event> {

    public BaseEventFeed setUpFeed() {

        if (dataSource == null) {
            dataSource = new EventDataSource(getActivity());
        }

        if (feedAdapter == null) {
            feedAdapter = new FeedAdapter<Event>(getActivity(), dataSource);
        }

        return this;
    }

}