package com.peck.android.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.peck.android.PeckApp;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class DatabaseManager {
    private static DatabaseManager dbCreator = new DatabaseManager(); //keep this. we need to instantiate to get our sqliteopenhelper
    private static final int version = 1;
    private static SQLiteDatabase database;
    private static SQLiteOpenHelper openHelper;

    //private static Class[] dbOperables = new Class[] { Event.class, Locale.class, Peck.class, Circle.class, User.class, Comment.class };

    public static synchronized SQLiteDatabase openDB() {
        database = openHelper.getWritableDatabase();
        return database;
    }

    public static synchronized void closeDB() {
        database.close();
    }

    private DatabaseManager() {
        openHelper = new SQLiteOpenHelper(PeckApp.getContext(), PeckApp.Constants.Database.DATABASE_NAME, null, version) {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {
                for (Class i : PeckApp.getModelArray()) sqLiteDatabase.execSQL(DBUtils.getDatabaseCreate(i));
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
                Log.w(this.getClass().getName(), "Upgrading DB from v." + oldVersion + " to v." + newVersion + "destroying all old data.");

                for (Class i : PeckApp.getModelArray()) {
                    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBUtils.getTableName(i));
                }
            }
        };
    }





}
