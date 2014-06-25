package com.peck.android.database.dataspec;

import com.peck.android.interfaces.Singleton;
import com.peck.android.models.User;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class UserDataSpec extends DataSpec<User> implements Singleton {

    private static UserDataSpec helper = new UserDataSpec();

    public static final String TABLE_NAME = "users";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_BIO = "bio";
    public static final String COLUMN_FACEBOOK_ID = "fb_id";

    {
        COLUMNS.put(COLUMN_BIO, "text");
        COLUMNS.put(COLUMN_FACEBOOK_ID, "integer");
        COLUMNS.put(COLUMN_NAME, "text not null");
    }

    public static UserDataSpec getInstance() {
        return helper;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public User generate() {
        return new User();
    }

}
