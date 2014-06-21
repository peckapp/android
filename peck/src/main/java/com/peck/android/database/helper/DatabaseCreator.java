package com.peck.android.database.helper;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.peck.android.PeckApp;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class DatabaseCreator extends SQLiteOpenHelper {
    private static DatabaseCreator dbCreator = new DatabaseCreator();
    private static int version = 1;

    private DataSourceHelper[] dbHelpers = {
            EventOpenHelper.getHelper(),
            FoodOpenHelper.getHelper(),
            MealOpenHelper.getHelper(),
            LocaleOpenHelper.getHelper(),
            PeckOpenHelper.getHelper(),
            CirclesOpenHelper.getHelper(),
            UserOpenHelper.getHelper()
    };

    public static String getDbName() {
        return PeckApp.Constants.Database.DATABASE_NAME;
    }

    public static int getDbVersion() {
        return version;
    }

    public static DatabaseCreator getDatabaseCreator() {
        return dbCreator;
    }

    private DatabaseCreator() {
        super(PeckApp.AppContext.getContext(), PeckApp.Constants.Database.DATABASE_NAME, null, version);
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
