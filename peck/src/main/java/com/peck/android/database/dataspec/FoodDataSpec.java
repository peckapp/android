package com.peck.android.database.dataspec;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Food;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class FoodDataSpec extends DataSpec<Food> implements Singleton {

    private static FoodDataSpec helper = new FoodDataSpec();

    public static final String TABLE_NAME = "food";

    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_MEAL_ID = "meal";
    public static final String COLUMN_TYPE = "type";

    {
        COLUMNS.put(COLUMN_TITLE, "text not null");
        COLUMNS.put(COLUMN_COLOR, "integer");
        COLUMNS.put(COLUMN_TEXT, "text");
        COLUMNS.put(COLUMN_MEAL_ID, "integer");
        COLUMNS.put(COLUMN_TYPE, "integer");
    }


    public static FoodDataSpec getInstance() {
       return helper;
   }

    public String getTableName() {
        return TABLE_NAME;
    }

    public Food generate() {
        return new Food();
    }

}
