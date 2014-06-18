package com.peck.android.database.helper;

import android.content.Context;

import com.peck.android.models.Circle;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class CirclesOpenHelper extends DataSourceHelper<Circle> {


    private static final String TAG = "circlesopenhelper";

    public static final String TABLE_NAME = "circles";

    public static final String COLUMN_LOC_ID = "loc_id";
    public static final String COLUMN_SERVER_ID = "sv_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_CREATED = "created_at";
    public static final String COLUMN_UPDATED = "updated_at";
    public static final String COLUMN_HIDDEN = "hidden";

    private final String[] ALL_COLUMNS = { COLUMN_LOC_ID, COLUMN_COLOR, COLUMN_CREATED, COLUMN_SERVER_ID,
            COLUMN_TITLE, COLUMN_UPDATED, COLUMN_HIDDEN};


    CirclesOpenHelper() {

    }

    public CirclesOpenHelper(Context context) {
        super(context, null);
    }

    @Override
    public String getColLocId() {
        return null;
    }

    @Override
    public String[] getColumns() {
        return ALL_COLUMNS;
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public String getDatabaseCreate() {
        return null;
    }
}
