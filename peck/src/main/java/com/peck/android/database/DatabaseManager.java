package com.peck.android.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.peck.android.PeckApp;
import com.peck.android.database.dataspec.CirclesDataSpec;
import com.peck.android.database.dataspec.DataSpec;
import com.peck.android.database.dataspec.EventDataSpec;
import com.peck.android.database.dataspec.FoodDataSpec;
import com.peck.android.database.dataspec.LocaleDataSpec;
import com.peck.android.database.dataspec.MealDataSpec;
import com.peck.android.database.dataspec.PeckDataSpec;
import com.peck.android.database.dataspec.UserDataSpec;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class DatabaseManager {
    private static DatabaseManager dbCreator = new DatabaseManager();
    private static final int version = 1;
    private static SQLiteDatabase database;
    private static SQLiteOpenHelper openHelper;
    private static int openCount = 0;

    private static ArrayList<DataSpec> dbSpecs = new ArrayList<DataSpec>();

    static {
        dbSpecs.add(EventDataSpec.getInstance());
        dbSpecs.add(FoodDataSpec.getInstance());
        dbSpecs.add(MealDataSpec.getInstance());
        dbSpecs.add(LocaleDataSpec.getInstance());
        dbSpecs.add(PeckDataSpec.getInstance());
        dbSpecs.add(CirclesDataSpec.getInstance());
        dbSpecs.add(UserDataSpec.getInstance());
    }

    public static String getDbName() {
        return PeckApp.Constants.Database.DATABASE_NAME;
    }

    public static int getDbVersion() {
        return version;
    }

    public static DatabaseManager getDatabaseCreator() {
        return dbCreator;
    }

    public static synchronized SQLiteDatabase openDB() {
        if (openCount == 0) {
            database = openHelper.getWritableDatabase();
            database.enableWriteAheadLogging();
        }
        openCount++;
        Log.i("DatabaseManager", "[" + openCount + "] opening db socket");
        return database;
    }

    public static synchronized void closeDB() {
        openCount--;
        if (openCount == 0) {
            database.close();
        }
        Log.i("DatabaseManager", "[" + openCount + "] closing db socket");
    }

    private DatabaseManager() {
        openHelper = new SQLiteOpenHelper(PeckApp.AppContext.getContext(), PeckApp.Constants.Database.DATABASE_NAME, null, version) {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {
                for (DataSpec i : dbSpecs) sqLiteDatabase.execSQL(i.getDbCreate());
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
                Log.w(this.getClass().getName(), "Upgrading DB from v." + oldVersion + " to v." + newVersion + "destroying all old data.");

                for (DataSpec i : dbSpecs) {
                    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ i.getTableName());
                }
            }
        };
    }





}
