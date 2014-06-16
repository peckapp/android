package com.peck.android.database.helper;

import android.content.Context;

import com.peck.android.models.Peck;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class PeckOpenHelper extends DataSourceHelper<Peck> {

    private static final String TAG = "MealOpenHelper";

    public static final String TABLE_NAME = "pecks";

    public static final String COLUMN_LOC_ID = "loc_id";
    public static final String COLUMN_SERVER_ID = "sv_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_UPDATED = "updated_at";
    public static final String COLUMN_SEEN = "seen";

    private final String[] ALL_COLUMNS = { COLUMN_LOC_ID, COLUMN_SERVER_ID, COLUMN_COLOR,
            COLUMN_TEXT, COLUMN_UPDATED, COLUMN_SEEN, COLUMN_TITLE};


    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "(" + COLUMN_LOC_ID
            + " integer primary key autoincrement, "
            + COLUMN_SERVER_ID + " integer, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_COLOR + " integer, "
            + COLUMN_SEEN + " bool, "
            + COLUMN_TEXT + " text, "
            + COLUMN_UPDATED + " integer"
            + ");";


    public PeckOpenHelper(Context context) {
        super(context, null);
    }

    PeckOpenHelper() {
        super();
    }

    @Override
    public String getColLocId() {
        return COLUMN_LOC_ID;
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
