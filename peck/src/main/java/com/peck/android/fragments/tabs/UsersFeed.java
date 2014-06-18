package com.peck.android.fragments.tabs;

import com.peck.android.R;
import com.peck.android.fragments.Feed;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.UserManager;
import com.peck.android.models.User;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class UsersFeed extends Feed<User> {

    @Override
    public Feed<User> setUpFeed() {
        return null;
    }

    @Override
    public int getListViewRes() {
        return R.id.riv_user;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.lvitem_user;
    }

    @Override
    public Class<? extends Singleton> getManagerClass() {
        return UserManager.class;
    }

    @Override
    public int getTabTag() { //don't worry about this; we don't need it
        return 0;
    }
}
