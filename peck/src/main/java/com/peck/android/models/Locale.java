package com.peck.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;

import com.peck.android.database.helper.LocaleOpenHelper;
import com.peck.android.interfaces.DBOperable;

/**
 * Created by mammothbane on 6/11/2014.
 */
public class Locale extends DBOperable {

    private int localId;
    private int serverId;
    private String name;
    private Location location;

    public Location getLocation() {
        return location;
    }

    public Locale setLocation(Location location) {
        this.location = location;
        return this;
    }

    public int getServerId() {
        return serverId;
    }

    public Locale setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Locale setName(String name) {
        this.name = name;
        return this;
    }

    public int getLocalId() {
        return localId;
    }

    public Locale setLocalId(int id) {
        this.localId = id;
        return this;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(LocaleOpenHelper.COLUMN_LOC_ID, getLocalId());
        cv.put(LocaleOpenHelper.COLUMN_SV_ID, getServerId());
        cv.put(LocaleOpenHelper.COLUMN_NAME, getName());
        cv.put(LocaleOpenHelper.COLUMN_LAT, getLocation().getLatitude());
        cv.put(LocaleOpenHelper.COLUMN_LONG, getLocation().getLongitude());
        return cv;
    }

    @Override
    public Locale fromCursor(Cursor cursor) {
        Location t = new Location("database");
        t.setLatitude(cursor.getDouble(cursor.getColumnIndex(LocaleOpenHelper.COLUMN_LAT)));
        t.setLongitude(cursor.getDouble(cursor.getColumnIndex(LocaleOpenHelper.COLUMN_LONG)));

        return this.setLocalId(cursor.getInt(cursor.getColumnIndex(LocaleOpenHelper.COLUMN_LOC_ID)))
                .setServerId(cursor.getInt(cursor.getColumnIndex(LocaleOpenHelper.COLUMN_SV_ID)))
                .setName(cursor.getString(cursor.getColumnIndex(LocaleOpenHelper.COLUMN_NAME)))
                .setLocation(t);
    }
}
