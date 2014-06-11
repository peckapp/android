package com.peck.android.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.peck.android.PeckApp;
import com.peck.android.interfaces.HasStatic;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class DatabaseCreator extends SQLiteOpenHelper {
    private static DatabaseCreator dbCreator;
    private static int version = 1;

    private DataSourceHelper[] dbHelpers = {
    //do *not* use these database helpers to access the database; they don't set up fully
            new EventOpenHelper(),
            new FoodOpenHelper(),
            new MealOpenHelper()
    };

    public static String getDbName() {
        return PeckApp.Constants.DATABASE_NAME;
    }

    public static int getDbVersion() {
        return version;
    }

    public static DatabaseCreator getDatabaseCreator(Context context) {
        if (dbCreator == null) dbCreator = new DatabaseCreator(context);
        return dbCreator;
    }

    private DatabaseCreator(Context context) {
        super(context, PeckApp.Constants.DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        for (DataSourceHelper i : dbHelpers) {
            database.execSQL(i.getDatabaseCreate());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(this.getClass().getName(), "Upgrading DB from v." + oldVersion + " to v." + newVersion + "destroying all old data.");

        for (DataSourceHelper i : dbHelpers) {
            database.execSQL("DROP TABLE IF EXISTS "+ i.getTableName());
        }

    }
}
