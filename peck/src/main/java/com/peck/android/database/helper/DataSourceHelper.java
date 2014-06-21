package com.peck.android.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.peck.android.PeckApp;
import com.peck.android.database.source.DataSource;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.Factory;
import com.peck.android.interfaces.Singleton;

/**
 * Created by mammothbane on 5/28/2014.
 */
public abstract class DataSourceHelper<T extends DBOperable> extends SQLiteOpenHelper implements Factory<T> {

    DataSource<T> dataSource;
    private static Context context;
    static {
        context = PeckApp.AppContext.getContext();
    }

    DataSourceHelper()
    {
        super(context, DatabaseCreator.getDbName(), null, DatabaseCreator.getDbVersion());
        if (!(this instanceof Singleton)) throw new ClassCastException("DataSourceHelpers *must* be singletons");

        try {
        if (getTableName() == null) throw new Exception("you must have a table name"); }
        catch (Exception e) {e.printStackTrace();}

    }




    @Override
    public void onCreate(SQLiteDatabase database) {
        DatabaseCreator.getDatabaseCreator().onCreate(database);
    }

    public void setDatasource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DatabaseCreator.getDatabaseCreator().onUpgrade(db, oldVersion, newVersion);
    }

    public abstract String getColLocId();
    public abstract String[] getColumns(); //return columns in a string array;
    public abstract String getTableName();
    public abstract String getDatabaseCreate();


}