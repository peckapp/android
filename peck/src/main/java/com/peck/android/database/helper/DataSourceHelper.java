package com.peck.android.database.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.peck.android.database.source.DataSource;
import com.peck.android.interfaces.WithLocal;

/**
 * Created by mammothbane on 5/28/2014.
 */
public abstract class DataSourceHelper<T extends WithLocal> extends SQLiteOpenHelper {



    DataSource<T, DataSourceHelper<T>> dataSource;
    private Context context;

    public DataSourceHelper(Context context, SQLiteDatabase.CursorFactory factory)
    {
        super(context, DatabaseCreator.getDbName(), factory, DatabaseCreator.getDbVersion());
        this.context = context;
        try {
        if (getTableName() == null) throw new Exception("you must have a table name"); }
        catch (Exception e) {e.printStackTrace();}

    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        DatabaseCreator.getDatabaseCreator(context).onCreate(database);
    }

    public void setDatasource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DatabaseCreator.getDatabaseCreator(context).onUpgrade(db, oldVersion, newVersion);
    }

    public abstract T createFromCursor(Cursor cursor);
    public abstract String getTableName();
    public abstract String getColLocId();
    public abstract String[] getColumns(); //return columns in a string array;


}