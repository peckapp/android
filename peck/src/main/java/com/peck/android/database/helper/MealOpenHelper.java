package com.peck.android.database.helper;

import android.content.Context;
import android.database.Cursor;

import com.peck.android.PeckApp;
import com.peck.android.models.Meal;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class MealOpenHelper extends DataSourceHelper<Meal> {

    private static final String TAG = "MealOpenHelper";

    //TODO: fix strings for meals
    public final String TABLE_NAME = "meals";
    public final String COLUMN_LOC_ID = "loc_id";
    public final String COLUMN_SERVER_ID = "sv_id";
    public final String COLUMN_TITLE = "title";
    public final String COLUMN_COLOR = "color";
    public final String COLUMN_CREATED = "created_at";
    public final String COLUMN_UPDATED = "updated_at";
    public final String COLUMN_HIDDEN = "hidden";

    private final String[] ALL_COLUMNS = { COLUMN_LOC_ID, COLUMN_SERVER_ID, COLUMN_COLOR,
            COLUMN_CREATED, COLUMN_UPDATED, COLUMN_HIDDEN, COLUMN_TITLE};

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

    public MealOpenHelper(Context context) {
        super(context, PeckApp.getDatabaseName(), null, DATABASE_VERSION);
    }

    public String getColLocId() {
        return COLUMN_LOC_ID;
    }

    public String getTableName() {
        return TABLE_NAME;
    }

    //TODO: implement this method
    public Meal createFromCursor(Cursor cursor) {
        return new Meal();
    }

    public String getDatabaseCreate() {
        return DATABASE_CREATE;
    }

    public String[] getColumns() {
        return ALL_COLUMNS;
    }

    public int getVersion() {
        return DATABASE_VERSION;
    }


}
