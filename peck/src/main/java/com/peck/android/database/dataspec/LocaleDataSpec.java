package com.peck.android.database.dataspec;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Locale;

/**
 * Created by mammothbane on 6/11/2014.
 */
public class LocaleDataSpec extends DataSpec<Locale> implements Singleton {

    private static LocaleDataSpec helper = new LocaleDataSpec();

    public final static String TABLE_NAME = "locales";
    public final static String COLUMN_LOC_ID = "loc_id";
    public final static String COLUMN_SV_ID = "sv_id";
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_LAT = "latitude";
    public final static String COLUMN_LONG = "longitude";

    public final String[] ALL_COLUMNS = { COLUMN_LOC_ID, COLUMN_NAME, COLUMN_SV_ID, COLUMN_LAT, COLUMN_LONG };


    private final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "("
            + COLUMN_SV_ID + " integer, "
            + COLUMN_LOC_ID + " integer primary key autoincrement, "
            + COLUMN_LAT + " double, "
            + COLUMN_LONG + " double, "
            + COLUMN_NAME + " text not null"
            + ");";

    public static LocaleDataSpec getHelper() {
        return helper;
    }

    @Override
    public String getColLocId() {
        return COLUMN_LOC_ID;
    }


    @Override
    public String[] getColumns() {
        return ALL_COLUMNS;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getDatabaseCreate() {
        return DATABASE_CREATE;
    }

    public Locale generate() {
        return new Locale();
    }

}
