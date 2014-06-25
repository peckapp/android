package com.peck.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.view.View;
import android.widget.TextView;

import com.peck.android.R;
import com.peck.android.database.dataspec.LocaleDataSpec;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

/**
 * Created by mammothbane on 6/11/2014.
 */
public class Locale extends DBOperable implements SelfSetup, HasFeedLayout {

    private final static int resId = R.layout.lvitem_locale;

    private int serverId = -1;
    private String name = "";
    private Location location = new Location("null");
    private double dist; //don't add to database

    public Location getLocation() {
        return location;
    }

    public double getDist() {
        return dist;
    }

    public Locale calcDist(Location l) {
        dist = Math.sqrt(Math.pow((l.getLongitude() - getLocation().getLongitude()), 2) + Math.pow((l.getLatitude() - getLocation().getLatitude()), 2));
        return this;
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

    public Locale setLocalId(int id) {
        this.localId = id;
        return this;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(LocaleDataSpec.COLUMN_SERVER_ID, getServerId());
        cv.put(LocaleDataSpec.COLUMN_NAME, getName());
        cv.put(LocaleDataSpec.COLUMN_LAT, getLocation().getLatitude());
        cv.put(LocaleDataSpec.COLUMN_LONG, getLocation().getLongitude());
        return cv;
    }

    @Override
    public Locale fromCursor(Cursor cursor) {
        Location t = new Location("database");
        t.setLatitude(cursor.getDouble(cursor.getColumnIndex(LocaleDataSpec.COLUMN_LAT)));
        t.setLongitude(cursor.getDouble(cursor.getColumnIndex(LocaleDataSpec.COLUMN_LONG)));

        return this.setLocalId(cursor.getInt(cursor.getColumnIndex(LocaleDataSpec.COLUMN_LOC_ID)))
                .setServerId(cursor.getInt(cursor.getColumnIndex(LocaleDataSpec.COLUMN_SERVER_ID)))
                .setName(cursor.getString(cursor.getColumnIndex(LocaleDataSpec.COLUMN_NAME)))
                .setLocation(t);
    }

    public String toString() {
        String ret = (name == null) ? "" : name;
        return ret + " @ [" + getLocation().getLatitude() + ", " + getLocation().getLongitude() + "]";
    }

    @Override
    public void setUp(View v) {
        ((TextView)v.findViewById(R.id.tv_locale_name)).setText(toString());
    }

    @Override
    public int getResourceId() {
        return resId;
    }
}
