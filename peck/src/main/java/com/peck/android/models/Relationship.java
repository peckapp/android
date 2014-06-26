package com.peck.android.models;

import android.content.ContentValues;
import android.database.Cursor;

import com.peck.android.interfaces.DBOperable;

/**
 * Created by mammothbane on 6/26/2014.
 */
public class Relationship<T extends DBOperable, S extends DBOperable> extends DBOperable {

    @Override
    public ContentValues toContentValues() {
        return null;
    }

    @Override
    public DBOperable fromCursor(Cursor cursor) {
        return null;
    }
}
