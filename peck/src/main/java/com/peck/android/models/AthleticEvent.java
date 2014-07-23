package com.peck.android.models;

import com.peck.android.annotations.Header;
import com.peck.android.annotations.UriPath;

/**
 * Created by mammothbane on 7/22/2014.
 */
@Header(singular = "athletic_event", plural = "athletic_events")
@UriPath("athletic_events")
public class AthleticEvent extends Event {

    {
        type = ATHLETIC_EVENT;
    }


}
