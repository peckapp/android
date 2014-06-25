package com.peck.android.database.dataspec;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Locale;

/**
 * Created by mammothbane on 6/11/2014.
 */
public class LocaleDataSpec extends DataSpec<Locale> implements Singleton {

    private static LocaleDataSpec helper = new LocaleDataSpec();

    public final static String TABLE_NAME = "locales";

    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_LAT = "latitude";
    public final static String COLUMN_LONG = "longitude";

    {
        COLUMNS.put(COLUMN_NAME, "text not null");
        COLUMNS.put(COLUMN_LAT, "integer not null");
        COLUMNS.put(COLUMN_LONG, "integer not null");
    }


    public static LocaleDataSpec getInstance() {
        return helper;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public Locale generate() {
        return new Locale();
    }

}
