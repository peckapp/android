package com.peck.android.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.peck.android.interfaces.WithLocal;

/**
 * Created by mammothbane on 5/28/2014.
 */
public abstract class DataSourceHelper<T extends WithLocal> extends SQLiteOpenHelper {

    DataSource<T, DataSourceHelper<T>> dataSource;

    public DataSourceHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
        try {
        if (getTableName() == null || getDatabaseCreate() == null) throw
                new Exception("you MUST have a database creation string and a table name\n" +
                        "table name " + ((getTableName() == null) ? "null" : getTableName()) +
                "\ndbcreate " + ((getDatabaseCreate() == null) ? "null" : getDatabaseCreate()));}
        catch (Exception e) {e.printStackTrace();}

    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(getDatabaseCreate());
    }

    public void setDatabase(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (getTableName() != null) {
            Log.w(this.getClass().getName(), "Upgrading DB from v." + oldVersion + " to v." + newVersion + "destroying all old data.");
            db.execSQL("DROP TABLE IF EXISTS " + getTableName());
            onCreate(db);
        }
    }

    protected abstract T createFromCursor(Cursor cursor);
    protected abstract String getTableName();
    protected abstract String getDatabaseCreate();
    protected abstract String getColLocId();
    protected abstract String[] getColumns(); //return columns in a string array;
    protected abstract int getVersion();


}