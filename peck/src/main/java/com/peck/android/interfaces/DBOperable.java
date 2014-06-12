package com.peck.android.interfaces;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by mammothbane on 5/28/2014.
 */
public abstract class DBOperable {

    public abstract int getLocalId();

    public abstract ContentValues toContentValues();

    public abstract DBOperable fromCursor(Cursor cursor);

}
