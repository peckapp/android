package com.peck.android.database.dataspec;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Meal;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class MealDataSpec extends DataSpec<Meal> implements Singleton {

    private static MealDataSpec helper = new MealDataSpec();

    public static final String TABLE_NAME = "meals";

    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_HIDDEN = "hidden";
    public static final String COLUMN_LOCATION_ID = "location";
    public static final String COLUMN_MEAL_TYPE = "mealtype";


    {
        COLUMNS.put(COLUMN_TIME, "integer");
        COLUMNS.put(COLUMN_HIDDEN, "integer");
        COLUMNS.put(COLUMN_LOCATION_ID, "integer");
        COLUMNS.put(COLUMN_MEAL_TYPE, "integer");
        COLUMNS.put(COLUMN_TITLE, "text not null");

    }


    public static MealDataSpec getInstance() {
        return helper;
    }

    public String getTableName() {
        return TABLE_NAME;
    }


    public Meal generate() {
        return new Meal();
    }


}
