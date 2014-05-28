package com.peck.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import com.peck.android.models.Event;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class EventDataSource {
    private SQLiteDatabase database;
    private EventOpenHelper dbHelper;
    private String[] allColumns = { EventOpenHelper.COLUMN_LOC_ID,
            EventOpenHelper.COLUMN_SERVER_ID,
            EventOpenHelper.COLUMN_COLOR,
            EventOpenHelper.COLUMN_CREATED,
            EventOpenHelper.COLUMN_UPDATED,
            EventOpenHelper.COLUMN_HIDDEN,
            EventOpenHelper.COLUMN_TITLE};

    public EventDataSource(Context context) {
        dbHelper = new EventOpenHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    public Event createEvent(String title, int color, int serverId, Date created, Date updated) {
        ContentValues values = new ContentValues();
        values.put(EventOpenHelper.COLUMN_SERVER_ID, serverId);
        values.put(EventOpenHelper.COLUMN_COLOR, color);
        values.put(EventOpenHelper.COLUMN_TITLE, title);
        values.put(EventOpenHelper.COLUMN_CREATED, created.getTime());
        values.put(EventOpenHelper.COLUMN_UPDATED, updated.getTime());
        values.put(EventOpenHelper.COLUMN_HIDDEN, 0);

        long insertId = database.insert(EventOpenHelper.TABLE_EVENTS, null, values);

        Cursor cursor = database.query(EventOpenHelper.TABLE_EVENTS, allColumns,
                EventOpenHelper.COLUMN_LOC_ID + " = " + insertId, null, null, null, null);

        Event newEvent = cursorToEvent(cursor);
        cursor.close();
        return newEvent;
    }
    
    public void updateEvent(Event event){
        ContentValues values = new ContentValues();
        values.put(EventOpenHelper.COLUMN_SERVER_ID, event.getLocalId());
        values.put(EventOpenHelper.COLUMN_COLOR, event.getColor());
        values.put(EventOpenHelper.COLUMN_TITLE, event.getTitle());

        database.update(EventOpenHelper.TABLE_EVENTS,
                values,
                EventOpenHelper.COLUMN_LOC_ID + " = ?",
                new String[]{String.valueOf(event.getLocalId())});
    }

    public void deleteEvent(Event event) {
        long id = event.getLocalId();
        System.out.println("Event deleted with id: " + id);
        database.delete(EventOpenHelper.TABLE_EVENTS, EventOpenHelper.COLUMN_LOC_ID
                + " = " + id, null);
    }

    public ArrayList<Event> getAllEvents() {
        ArrayList<Event> events = new ArrayList<Event>();

        Cursor cursor = database.query(EventOpenHelper.TABLE_EVENTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Event event = cursorToEvent(cursor);
            events.add(event);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return events;
    }

    private Event cursorToEvent(Cursor cursor) {
        Event event = new Event();
        event.setLocalId(cursor.getInt(cursor.getColumnIndex(EventOpenHelper.COLUMN_LOC_ID)));
        event.setServerId(cursor.getInt(cursor.getColumnIndex(EventOpenHelper.COLUMN_SERVER_ID)));
        event.setColor(cursor.getInt(cursor.getColumnIndex(EventOpenHelper.COLUMN_COLOR)));
        event.setTitle(cursor.getString(cursor.getColumnIndex(EventOpenHelper.COLUMN_TITLE)));
        return event;
    }
}
