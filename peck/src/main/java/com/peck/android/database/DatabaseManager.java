package com.peck.android.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.peck.android.PeckApp;
import com.peck.android.json.JsonConverter;
import com.peck.android.models.Circle;
import com.peck.android.models.Comment;
import com.peck.android.models.DBOperable;
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

    private static ArrayList<DBOperable> dbOperables = new ArrayList<DBOperable>();

    static {
        dbOperables.add(new Event());
        dbOperables.add(new Locale());
        dbOperables.add(new Peck());
        dbOperables.add(new Circle());
        dbOperables.add(new User());
        dbOperables.add(new Comment());
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
                for (DBOperable i : dbOperables) sqLiteDatabase.execSQL(JsonConverter.getDatabaseCreate(i));
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
                Log.w(this.getClass().getName(), "Upgrading DB from v." + oldVersion + " to v." + newVersion + "destroying all old data.");

                for (DBOperable i : dbOperables) {
                    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + i.getTableName());
                }
            }
        };
    }





}
