package com.peck.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class EventOpenHelper extends SQLiteOpenHelper {


    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_LOC_ID = "loc_id";
    public static final String COLUMN_SERVER_ID = "sv_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_CREATED = "created_at";
    public static final String COLUMN_UPDATED = "updated_at";
    public static final String COLUMN_HIDDEN = "hidden";

    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 1;

    // sql create database command
    private static final String DATABASE_CREATE = "create table "
            + TABLE_EVENTS + "(" + COLUMN_LOC_ID
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


    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(EventOpenHelper.class.getName(), "Upgrading DB from v." + oldVersion + " to v." + newVersion + "destroying all old data.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);

    }




}
