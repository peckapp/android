package com.peck.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.view.View;

import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.SelfSetup;

/**
 * Created by mammothbane on 6/12/2014.
 */
public class Profile extends DBOperable implements SelfSetup {

    //todo: extend user

    //todo: implement inherited methods


    @Override
    public void setUp(View v) {
        //if


    }

    @Override
    public int getLocalId() {
        return 0;
    }

    @Override
    public ContentValues toContentValues() {
        return null;
    }

    @Override
    public DBOperable fromCursor(Cursor cursor) {
        return null;
    }
}
