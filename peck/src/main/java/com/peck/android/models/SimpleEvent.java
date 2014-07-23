package com.peck.android.models;

import com.peck.android.annotations.Header;
import com.peck.android.annotations.UriPath;

/**
 * Created by mammothbane on 5/28/2014.
 */
@Header(plural = "simple_events", singular = "simple_event")
@UriPath("simple_events")
public class SimpleEvent extends Event {

    {
        type = SIMPLE_EVENT;
    }

}
