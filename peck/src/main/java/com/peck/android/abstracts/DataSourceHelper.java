package com.peck.android.abstracts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mammothbane on 5/28/2014.
 */
public abstract class DataSourceHelper<T> extends SQLiteOpenHelper {

    public DataSourceHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    { super(context, name, factory, version); }


}
