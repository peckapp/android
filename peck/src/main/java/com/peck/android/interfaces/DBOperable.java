package com.peck.android.interfaces;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

/**
 * Created by mammothbane on 5/28/2014.
 */
public abstract class DBOperable {

    protected int localId = -1;

    public int getLocalId() {
        return localId;
    }
    public DBOperable setLocalId(int id) {
        localId = id;
        return this;
    }

    public abstract ContentValues toContentValues();

    public abstract DBOperable fromCursor(Cursor cursor);

    public static long dateToInt(Date date) {
        if (date == null) return 0;
        else return date.getTime();
    }

}
