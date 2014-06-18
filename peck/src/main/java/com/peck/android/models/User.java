package com.peck.android.models;

import android.content.ContentValues;
import android.database.Cursor;

import com.peck.android.interfaces.DBOperable;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class User extends DBOperable {

    @Override
    public ContentValues toContentValues() {
        return null;
    }

    @Override
    public DBOperable fromCursor(Cursor cursor) {
        return null;
    }
}
