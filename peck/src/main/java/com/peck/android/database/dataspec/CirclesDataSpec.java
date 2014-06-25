package com.peck.android.database.dataspec;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Circle;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class CirclesDataSpec extends DataSpec<Circle> implements Singleton {

    private static CirclesDataSpec helper = new CirclesDataSpec();
    private static final String TAG = "circlesopenhelper";

    public static final String TABLE_NAME = "circles";

    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_HIDDEN = "hidden";

    {
        COLUMNS.put(COLUMN_TITLE, "text not null");
        COLUMNS.put(COLUMN_COLOR, "integer");
        COLUMNS.put(COLUMN_HIDDEN, "integer");
    }


    public static CirclesDataSpec getInstance() {
        return helper;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }


    public Circle generate() { return new Circle(); }
}
