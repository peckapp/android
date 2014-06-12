package com.peck.android.database.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.peck.android.PeckApp;
import com.peck.android.models.Event;

import java.util.Date;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class EventOpenHelper extends DataSourceHelper<Event> {

    private static final String TAG = "eventopenhelper";

    public static final String TABLE_NAME = "events";
    public static final String COLUMN_LOC_ID = "loc_id";
    public static final String COLUMN_SERVER_ID = "sv_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_CREATED = "created_at";
    public static final String COLUMN_UPDATED = "updated_at";
    public static final String COLUMN_HIDDEN = "hidden";
    public static final String COLUMN_TEXT = "text";

    private final String[] ALL_COLUMNS = { COLUMN_LOC_ID, COLUMN_SERVER_ID, COLUMN_COLOR,
            COLUMN_CREATED, COLUMN_UPDATED, COLUMN_HIDDEN, COLUMN_TITLE, COLUMN_TEXT};

    // sql create database command
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "(" + COLUMN_LOC_ID
            + " integer primary key autoincrement, "
            + COLUMN_SERVER_ID + " integer, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_COLOR + " integer, "
            + COLUMN_HIDDEN + " integer, "
            + COLUMN_CREATED + " integer, "
            + COLUMN_TEXT + " text, "
            + COLUMN_UPDATED + " integer"
            + ");";


    public EventOpenHelper(Context context) {
        super(context, null);
    }

    EventOpenHelper() { super(); }


    //TODO: fix, and write a working implementation for unit testing
//    //TEST: remove after testing
//    public EventOpenHelper(Context context, String test_name) {
//        super(context);
//    }


    public Event createFromCursor(Cursor cursor) {
        Event e = new Event();
        cursor.moveToFirst();

        return e.setLocalId(cursor.getInt(cursor.getColumnIndex(getColLocId())))
                .setServerId(cursor.getInt(cursor.getColumnIndex(COLUMN_SERVER_ID)))
                .setColor(cursor.getInt(cursor.getColumnIndex(COLUMN_COLOR)))
                .setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)))
                .setCreated(new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED))))
                .setUpdated(new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_UPDATED))))
                .setText(cursor.getString(cursor.getColumnIndex(COLUMN_TEXT)));
    }

    public String getTableName() {
        return TABLE_NAME;
    }

    public String getDatabaseCreate() {
        return DATABASE_CREATE;
    }

    public String getColLocId() {
        return COLUMN_LOC_ID;
    }

    public String[] getColumns() {
        return ALL_COLUMNS;
    }



}