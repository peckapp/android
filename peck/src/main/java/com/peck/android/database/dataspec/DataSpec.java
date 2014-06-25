package com.peck.android.database.dataspec;

import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.Factory;
import com.peck.android.interfaces.Singleton;

/**
 * Created by mammothbane on 5/28/2014.
 */
public abstract class DataSpec<T extends DBOperable> implements Factory<T> {

    DataSpec()
    {
        if (!(this instanceof Singleton)) throw new ClassCastException("DataSourceHelpers *must* be singletons");

        try {
        if (getTableName() == null) throw new Exception("you must have a table name"); }
        catch (Exception e) {e.printStackTrace();}

    }

    public abstract String getColLocId();
    public abstract String[] getColumns(); //return columns in a string array;
    public abstract String getTableName();
    public abstract String getDatabaseCreate();


}