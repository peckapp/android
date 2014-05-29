package com.peck.android.abstracts;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.peck.android.database.DataSource;

/**
 * Created by mammothbane on 5/28/2014.
 */
public abstract class DataSourceHelper<T> extends SQLiteOpenHelper {

    public String TABLE_NAME; //name of the table
    public String COLUMN_LOC_ID;
    protected String DATABASE_CREATE;
    protected DataSource dataSource;


    public abstract String[] getColumns(); //return columns in a string array

    public DataSourceHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
        try {
        if (TABLE_NAME == null || DATABASE_CREATE == null) throw
                new Exception("you MUST have a database creation string and a table name");}
        catch (Exception e) {e.printStackTrace();}

    }

    public abstract T createFromCursor(Cursor cursor);


    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public void setDatabase(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (TABLE_NAME != null) {
            Log.w(this.getClass().getName(), "Upgrading DB from v." + oldVersion + " to v." + newVersion + "destroying all old data.");
            db.execSQL("DROP TABLE IF EXISTS " + this.TABLE_NAME);
            onCreate(db);
        }
    }


}