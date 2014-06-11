package com.peck.android.database.helper;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;

import com.peck.android.models.Locale;

/**
 * Created by mammothbane on 6/11/2014.
 */
public class LocaleOpenHelper extends DataSourceHelper {

    public final static String TABLE_NAME = "locales";
    public final static String COLUMN_LOC_ID = "loc_id";
    public final static String COLUMN_SV_ID = "sv_id";
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_LAT = "latitutde";
    public final static String COLUMN_LONG = "longitude";

    public final String[] ALL_COLUMNS = { COLUMN_LOC_ID, COLUMN_NAME, COLUMN_SV_ID, COLUMN_LAT, COLUMN_LONG };


    private final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "("
            + COLUMN_LOC_ID + " integer primary key autoincrement, "
            + COLUMN_LAT + " double, "
            + COLUMN_LONG + " double, "

            + COLUMN_NAME + " text not null"
            + ");";


    public LocaleOpenHelper(Context context) {
        super(context, null);
    }

    LocaleOpenHelper() {
        super();
    }

    @Override
    public String getColLocId() {
        return COLUMN_LOC_ID;
    }

    @Override
    public Locale createFromCursor(Cursor cursor) {
        Location t = new Location("database");
        t.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LAT)));
        t.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LONG)));

        return new Locale().setLocalId(cursor.getInt(cursor.getColumnIndex(COLUMN_LOC_ID)))
            .setServerId(cursor.getInt(cursor.getColumnIndex(COLUMN_SV_ID)))
                .setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)))
                .setLocation(t);
    }

    @Override
    public String[] getColumns() {
        return ALL_COLUMNS;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getDatabaseCreate() {
        return DATABASE_CREATE;
    }
}
