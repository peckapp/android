package com.peck.android.database.source;

import android.content.Context;

import com.peck.android.database.helper.EventOpenHelper;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 6/9/2014.
 */
public class EventDataSource extends DataSource<Event, EventOpenHelper> {

    private Context context;
    private EventOpenHelper dbHelper;

    public EventDataSource(Context context) {
        super(new EventOpenHelper(context));
    }

}
