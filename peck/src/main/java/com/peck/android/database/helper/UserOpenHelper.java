package com.peck.android.database.helper;

import com.peck.android.models.User;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class UserOpenHelper extends DataSourceHelper<User> {

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
