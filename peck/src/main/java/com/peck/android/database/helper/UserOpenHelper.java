package com.peck.android.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.peck.android.models.User;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class UserOpenHelper extends DataSourceHelper<User> {

    public UserOpenHelper(Context context) {
        super(context, null);
    }

    public UserOpenHelper() {
    }

    @Override
    public String getColLocId() {
        return null;
    }

    @Override
    public String[] getColumns() {
        return new String[0];
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public String getDatabaseCreate() {
        return null;
    }
}
