package com.peck.android.database.dataspec;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Event;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class EventDataSpec extends DataSpec<Event> implements Singleton {

    private static EventDataSpec helper = new EventDataSpec();

    public static final String TABLE_NAME = "events";

    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_HIDDEN = "hidden";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_IMAGE_URL = "image_url";
    public static final String COLUMN_EVENT_URL = "event_url";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";

    {
        COLUMNS.put(COLUMN_TITLE, "text not null");
        COLUMNS.put(COLUMN_HIDDEN, "integer");
        COLUMNS.put(COLUMN_TEXT, "text");
        COLUMNS.put(COLUMN_IMAGE_URL, "text");
        COLUMNS.put(COLUMN_EVENT_URL, "text");
        COLUMNS.put(COLUMN_START_DATE, "integer");
        COLUMNS.put(COLUMN_END_DATE, "end_date");
    }


    public static EventDataSpec getInstance() {
        return helper;
    }

    public String getTableName() {
        return TABLE_NAME;
    }

    public Event generate() {
        return new Event();
    }

}