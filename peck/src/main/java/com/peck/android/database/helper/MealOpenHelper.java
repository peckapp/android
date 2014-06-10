package com.peck.android.database.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.peck.android.PeckApp;
import com.peck.android.models.Meal;

import java.util.Date;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class MealOpenHelper extends DataSourceHelper<Meal> {

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

    private Context context;

    public MealOpenHelper(Context context) {
        super(context, null);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(getDatabaseCreate());
        database.execSQL(new FoodOpenHelper(context).getDatabaseCreate());
    }

    public String getColLocId() {
        return COLUMN_LOC_ID;
    }

    public String getTableName() {
        return TABLE_NAME;
    }

    //TODO: implement this method
    public Meal createFromCursor(Cursor cursor) {
        Meal m = new Meal();
        return m.setServerId(cursor.getInt(cursor.getColumnIndex(COLUMN_SERVER_ID)))
                .setType(cursor.getInt(cursor.getColumnIndex(COLUMN_MEAL_TYPE)))
                .setColor(cursor.getInt(cursor.getColumnIndex(COLUMN_COLOR)))
                .setServerId(cursor.getInt(cursor.getColumnIndex(COLUMN_SERVER_ID)))
                .setMealtime(new Date(cursor.getInt(cursor.getColumnIndex(COLUMN_TIME))))
                .setUpdated(new Date(cursor.getInt(cursor.getColumnIndex(COLUMN_UPDATED))))
                .setLocalId(cursor.getInt(cursor.getColumnIndex(COLUMN_LOC_ID)))
                .setLocation(cursor.getInt(cursor.getColumnIndex(COLUMN_LOCATION_ID)))
                .setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
    }

    public static String getDatabaseCreate() {
        return DATABASE_CREATE;
    }

    public String[] getColumns() {
        return ALL_COLUMNS;
    }


}
