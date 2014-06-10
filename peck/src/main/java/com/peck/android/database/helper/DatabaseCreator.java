package com.peck.android.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class DatabaseCreator extends SQLiteOpenHelper {
    private static DatabaseCreator dbCreator;
    private static int version = 1;
    private static final String DATABASE_NAME = "peck.db";

    public static String getDbName() {
        return DATABASE_NAME;
    }

    public static int getDbVersion() {
        return version;
    }

    public static DatabaseCreator getDatabaseCreator(Context context) {
        if (dbCreator == null) dbCreator = new DatabaseCreator(context);
        return dbCreator;
    }

    private DatabaseCreator(Context context) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(MealOpenHelper.getDatabaseCreate());
        database.execSQL(FoodOpenHelper.getDatabaseCreate());
        database.execSQL(EventOpenHelper.getDatabaseCreate());
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(this.getClass().getName(), "Upgrading DB from v." + oldVersion + " to v." + newVersion + "destroying all old data.");
        database.execSQL("DROP TABLE IF EXISTS " + EventOpenHelper.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + MealOpenHelper.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + FoodOpenHelper.TABLE_NAME);
        onCreate(database);
    }
}
