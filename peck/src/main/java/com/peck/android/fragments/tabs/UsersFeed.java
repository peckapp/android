package com.peck.android.fragments.tabs;

import com.peck.android.R;
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.DataSource;
import com.peck.android.database.dataspec.UserDataSpec;
import com.peck.android.fragments.Feed;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.UserManager;
import com.peck.android.models.User;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class UsersFeed extends Feed<User> {

    @Override
    public UsersFeed setUpFeed() {
        //call manager to associate users

        if (dataSource == null) {
            dataSource = new DataSource<User>(UserDataSpec.getInstance());
        }

        if (feedAdapter == null) {
            feedAdapter = new FeedAdapter<User>(dataSource.generate().getResourceId());
        }

        return this;
    }

    @Override
    public int getListViewRes() {
        return R.id.hlv_users;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.lvitem_circle;
    }

    @Override
    public Class<? extends Singleton> getManagerClass() {
        return UserManager.class;
    }

}
