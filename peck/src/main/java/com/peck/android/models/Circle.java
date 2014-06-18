package com.peck.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.view.View;

import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/12/2014.
 */
public class Circle extends DBOperable implements SelfSetup, HasFeedLayout {

    private ArrayList<User> users = new ArrayList<User>();

    //TODO: implement inherited methods

    @Override
    public void setUp(View v) {

    }

    @Override
    public int getResourceId() {
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
