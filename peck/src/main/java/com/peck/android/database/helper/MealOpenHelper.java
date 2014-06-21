package com.peck.android.database.helper;

import com.peck.android.models.Meal;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class MealOpenHelper extends DataSourceHelper<Meal> {

    private static MealOpenHelper helper = new MealOpenHelper();
    private static final String TAG = "MealOpenHelper";

    //TODO: fix strings for meals
    public static final String TABLE_NAME = "meals";
    public static final String COLUMN_LOC_ID = "loc_id";
    public static final String COLUMN_SERVER_ID = "sv_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_UPDATED = "updated_at";
    public static final String COLUMN_HIDDEN = "hidden";
    public static final String COLUMN_LOCATION_ID = "location";
    public static final String COLUMN_MEAL_TYPE = "mealtype";

    private final String[] ALL_COLUMNS = { COLUMN_LOC_ID, COLUMN_SERVER_ID, COLUMN_COLOR,
            COLUMN_UPDATED, COLUMN_HIDDEN, COLUMN_TITLE, COLUMN_LOCATION_ID, COLUMN_MEAL_TYPE};


    private static final String DATABASE_NAME = "dining.db";
    private static final int DATABASE_VERSION = 1;

    // sql create table command
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "(" + COLUMN_LOC_ID
            + " integer primary key autoincrement, "
            + COLUMN_SERVER_ID + " integer, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_COLOR + " integer, "
            + COLUMN_HIDDEN + " integer, "
            + COLUMN_TIME + " integer, "
            + COLUMN_LOCATION_ID + " integer, "
            + COLUMN_MEAL_TYPE + " integer, "
            + COLUMN_UPDATED + " integer"
            + ");";

    public static MealOpenHelper getHelper() {
        return helper;
    }


    public String getColLocId() {
        return COLUMN_LOC_ID;
    }

    public String getTableName() {
        return TABLE_NAME;
    }

    public String getDatabaseCreate() {
        return DATABASE_CREATE;
    }

    public String[] getColumns() {
        return ALL_COLUMNS;
    }


}
