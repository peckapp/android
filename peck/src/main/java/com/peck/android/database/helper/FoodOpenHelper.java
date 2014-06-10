package com.peck.android.database.helper;

import android.content.Context;
import android.database.Cursor;

import com.peck.android.PeckApp;
import com.peck.android.models.Food;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class FoodOpenHelper extends DataSourceHelper<Food> {

    private Context context;
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

    public FoodOpenHelper(Context context) {
        super(context, null);
        this.context = context;
    }

    //TODO: implement this method
    public Food createFromCursor(Cursor cursor) { //does not link to meal
        Food f = new Food();
        cursor.moveToFirst();
        return f.setServerId(cursor.getInt(cursor.getColumnIndex(COLUMN_SERVER_ID)))
                .setColor(cursor.getInt(cursor.getColumnIndex(COLUMN_COLOR)))
                .setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)))
                .setText(cursor.getString(cursor.getColumnIndex(COLUMN_TEXT)))
                .setMealId(cursor.getInt(cursor.getColumnIndex(COLUMN_MEAL_ID)))
                .setType(cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE)));
    }


    public String getColLocId() {
        return COLUMN_LOC_ID;
    }

    public String getTableName() {
        return TABLE_NAME;
    }

    public static String getDatabaseCreate() {
        return DATABASE_CREATE;
    }

    public String[] getColumns() {
        return ALL_COLUMNS;
    }

}
