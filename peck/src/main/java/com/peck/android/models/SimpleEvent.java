package com.peck.android.models;

import com.peck.android.annotations.Header;
import com.peck.android.annotations.Table;

/**
 * Created by mammothbane on 5/28/2014.
 */
@Header(plural = "simple_events", singular = "simple_event")
@Table("events")
public class SimpleEvent extends Event {

    {
        type = SIMPLE_EVENT;
    }

}
