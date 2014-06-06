package com.peck.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.peck.android.models.Event;

import java.util.Date;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class EventOpenHelper extends DataSourceHelper<Event> {

    private static final String TAG = "eventopenhelper";

    public final String TABLE_NAME = "events";
    public final String COLUMN_LOC_ID = "loc_id";
    public final String COLUMN_SERVER_ID = "sv_id";
    public final String COLUMN_TITLE = "title";
    public final String COLUMN_COLOR = "color";
    public final String COLUMN_CREATED = "created_at";
    public final String COLUMN_UPDATED = "updated_at";
    public final String COLUMN_HIDDEN = "hidden";

    private final String[] ALL_COLUMNS = { COLUMN_LOC_ID, COLUMN_SERVER_ID, COLUMN_COLOR,
            COLUMN_CREATED, COLUMN_UPDATED, COLUMN_HIDDEN, COLUMN_TITLE};

    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 1;

    // sql create database command
    private final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "(" + COLUMN_LOC_ID
            + " integer primary key autoincrement, "
            + COLUMN_SERVER_ID + " integer, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_COLOR + " integer, "
            + COLUMN_HIDDEN + " integer, "
            + COLUMN_CREATED + " integer, "
            + COLUMN_UPDATED + " integer"
            + ");";


    public EventOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //TODO: remove after testing
    public EventOpenHelper(Context context, String test_name) {
        super(context, test_name, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(EventOpenHelper.class.getName(), "Upgrading DB from v." + oldVersion + " to v." + newVersion + "destroying all old data.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void update(Event e) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SERVER_ID, e.getLocalId());
        values.put(COLUMN_COLOR, e.getColor());
        values.put(COLUMN_TITLE, e.getTitle());
        dataSource.update(values, e.getLocalId());
    }

    public Event create(String title, int color, int serverId, Date created, Date updated) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SERVER_ID, serverId);
        values.put(COLUMN_COLOR, color);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_CREATED, created.getTime());
        values.put(COLUMN_UPDATED, updated.getTime());
        values.put(COLUMN_HIDDEN, 0);

        return (Event)dataSource.create(values);
    }

    public Event createFromCursor(Cursor cursor) {
        Event e = new Event();
        cursor.moveToFirst();
        Log.d(TAG, Integer.toString(cursor.getColumnIndex(getColLocId())));
        e.setLocalId(cursor.getInt(cursor.getColumnIndex(getColLocId())));
        e.setServerId(cursor.getInt(cursor.getColumnIndex(COLUMN_SERVER_ID)));
        e.setColor(cursor.getInt(cursor.getColumnIndex(COLUMN_COLOR)));
        e.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
        e.setCreated(new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED))));
        e.setUpdated(new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_UPDATED))));
        return e;
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