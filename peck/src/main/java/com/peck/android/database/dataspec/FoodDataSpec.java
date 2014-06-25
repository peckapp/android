package com.peck.android.database.dataspec;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Food;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class FoodDataSpec extends DataSpec<Food> implements Singleton {

    private static FoodDataSpec helper = new FoodDataSpec();
    private static final String TAG = "MealOpenHelper";

    //TODO: fix strings for meals
    public static final String TABLE_NAME = "food";
    public static final String COLUMN_LOC_ID = "loc_id";
    public static final String COLUMN_SERVER_ID = "sv_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_MEAL_ID = "meal";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_UPDATED_AT = "updated";

    private final String[] ALL_COLUMNS = {
            COLUMN_LOC_ID, COLUMN_SERVER_ID, COLUMN_COLOR, COLUMN_TITLE, COLUMN_TEXT,
            COLUMN_TYPE, COLUMN_MEAL_ID, COLUMN_UPDATED_AT};

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dining.db";

    // sql create database command
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "(" + COLUMN_LOC_ID
            + " integer primary key autoincrement, "
            + COLUMN_SERVER_ID + " integer, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_COLOR + " integer, "
            + COLUMN_TEXT + " text, "
            + COLUMN_MEAL_ID + " integer, "
            + COLUMN_UPDATED_AT + " integer, "
            + COLUMN_TYPE + " integer"
            + ");";

    public static FoodDataSpec getInstance() {
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

    public Food generate() {
        return new Food();
    }

}
