package com.peck.android.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.peck.android.PeckApp;
import com.peck.android.models.AthleticEvent;
import com.peck.android.models.Circle;
import com.peck.android.models.Comment;
import com.peck.android.models.Locale;
import com.peck.android.models.Peck;
import com.peck.android.models.SimpleEvent;
import com.peck.android.models.User;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class DatabaseManager {
    private static DatabaseManager dbCreator = new DatabaseManager();
    private static final int version = 1;
    private static SQLiteDatabase database;
    private static SQLiteOpenHelper openHelper;

    private static Class[] dbOperables = new Class[] { SimpleEvent.class, AthleticEvent.class, Locale.class, Peck.class, Circle.class, User.class, Comment.class };

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
                for (Class i : dbOperables) sqLiteDatabase.execSQL(DBUtils.getDatabaseCreate(i));
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
                Log.w(this.getClass().getName(), "Upgrading DB from v." + oldVersion + " to v." + newVersion + "destroying all old data.");

                for (Class i : dbOperables) {
                    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBUtils.getTableName(i));
                }
            }
        };
    }





}
