package com.peck.android.interfaces;

import android.database.Cursor;

/**
 * Created by mammothbane on 5/28/2014.
 */
public interface CursorCreatable<T> {

    int getLocalId();

    public T createFromCursor(Cursor cursor);

}
