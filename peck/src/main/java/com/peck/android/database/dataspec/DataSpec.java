package com.peck.android.database.dataspec;

import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.Factory;
import com.peck.android.interfaces.Singleton;

import java.util.HashMap;

/**
 * Created by mammothbane on 5/28/2014.
 */
public abstract class DataSpec<T extends DBOperable> implements Factory<T> {

    public static final String COLUMN_LOC_ID = "loc_id";
    public static final String COLUMN_SERVER_ID = "sv_id";
    public static final String COLUMN_CREATED = "created_at";
    public static final String COLUMN_UPDATED = "updated_at";
    private static final String DELIM = ", ";

    final HashMap<String, String> COLUMNS = new HashMap<String, String>();

    {
        COLUMNS.put(COLUMN_LOC_ID, "integer primary key autoincrement");
        COLUMNS.put(COLUMN_SERVER_ID, "integer");
        COLUMNS.put(COLUMN_CREATED, "integer");
        COLUMNS.put(COLUMN_UPDATED, "integer");
    }

    DataSpec()
    {
        if (!(this instanceof Singleton)) throw new ClassCastException("DataSourceHelpers *must* be singletons");

        try {
        if (getTableName() == null) throw new Exception("you must have a table name"); }
        catch (Exception e) {e.printStackTrace();}

    }

    public String[] getColumns() {
        synchronized (COLUMNS) {
            String[] ret = new String[COLUMNS.keySet().size()];
            COLUMNS.keySet().toArray(ret);
            return ret;
        }
    }

    public String getDbCreate() {
        String dbCreate = "create table " + getTableName() + " (";
        HashMap<String, String> map = new HashMap<String, String>(getMap());

        for ( String string : map.keySet() ) {
            dbCreate += (string + " " + map.get(string) + DELIM);
        }

        dbCreate = dbCreate.substring(0, (dbCreate.length() - DELIM.length())) + ");";

        //todo: make this work -- dbCreate += "constraint unq unique (" + DataSpec.COLUMN_SERVER_ID + "));";
        return dbCreate;
    }

    public HashMap<String, String> getMap() {
        synchronized (COLUMNS) {
            return COLUMNS;
        }
    }


    public abstract String getTableName();


}