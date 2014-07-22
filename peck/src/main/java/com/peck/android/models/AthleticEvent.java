package com.peck.android.models;

import com.peck.android.annotations.Header;
import com.peck.android.annotations.Table;

/**
 * Created by mammothbane on 7/22/2014.
 */
@Header(singular = "athletic_event", plural = "athletic_events")
@Table("events")
public class AthleticEvent extends Event {

    {
        type = ATHLETIC_EVENT;
    }


}
