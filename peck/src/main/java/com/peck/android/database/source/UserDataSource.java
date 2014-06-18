package com.peck.android.database.source;

import android.content.Context;

import com.peck.android.database.helper.DataSourceHelper;
import com.peck.android.database.helper.UserOpenHelper;
import com.peck.android.models.User;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class UserDataSource extends DataSource<User> {

    public UserDataSource(Context context) {
        super(new UserOpenHelper(context));
    }

    @Override
    public User generate() {
        return new User();
    }
}
