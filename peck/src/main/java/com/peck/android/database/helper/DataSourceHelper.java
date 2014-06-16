package com.peck.android.database.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.peck.android.database.source.DataSource;
import com.peck.android.interfaces.DBOperable;

/**
 * Created by mammothbane on 5/28/2014.
 */
public abstract class DataSourceHelper<T extends DBOperable> extends SQLiteOpenHelper {

    DataSource<T> dataSource;
    private Context context;

    public DataSourceHelper(Context context, SQLiteDatabase.CursorFactory factory)
    {
        super(context, DatabaseCreator.getDbName(), factory, DatabaseCreator.getDbVersion());
        this.context = context;
        try {
        if (getTableName() == null) throw new Exception("you must have a table name"); }
        catch (Exception e) {e.printStackTrace();}

    }

    DataSourceHelper() {
        super(null, DatabaseCreator.getDbName(), null, DatabaseCreator.getDbVersion());
        //this constructor is **only** to access individual instantiations for getTableName()
        //and getCreateSql
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

    public abstract String getColLocId();
    public abstract String[] getColumns(); //return columns in a string array;
    public abstract String getTableName();
    public abstract String getDatabaseCreate();


}