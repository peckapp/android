package com.peck.android.database.dataspec;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Peck;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class PeckDataSpec extends DataSpec<Peck> implements Singleton {

    private static PeckDataSpec helper = new PeckDataSpec();

    public static final String TABLE_NAME = "pecks";

    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_SEEN = "seen";

    {
        COLUMNS.put(COLUMN_TITLE, "text not null");
        COLUMNS.put(COLUMN_TEXT, "text");
        COLUMNS.put(COLUMN_COLOR, "integer");
        COLUMNS.put(COLUMN_SEEN, "integer");
    }

    public static PeckDataSpec getInstance() {
        return helper;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public Peck generate() {
        return new Peck();
    }

}
