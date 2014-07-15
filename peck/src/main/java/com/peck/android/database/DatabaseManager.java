package com.peck.android.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.peck.android.PeckApp;
import com.peck.android.models.Circle;
import com.peck.android.models.Comment;
import com.peck.android.models.Event;
import com.peck.android.models.Locale;
import com.peck.android.models.Peck;
import com.peck.android.models.User;

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

    private static ArrayList<Class> dbOperables = new ArrayList<Class>();

    static {
        dbOperables.add(Event.class);
        dbOperables.add(Locale.class);
        dbOperables.add(Peck.class);
        dbOperables.add(Circle.class);
        dbOperables.add(User.class);
        dbOperables.add(Comment.class);
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
        Log.d("DatabaseManager", "[" + openCount + "] closing db socket");
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
