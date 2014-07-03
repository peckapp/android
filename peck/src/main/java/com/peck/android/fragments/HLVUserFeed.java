package com.peck.android.fragments;

import com.peck.android.R;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.Manager;
import com.peck.android.managers.UserManager;
import com.peck.android.models.User;

/**
 * Created by mammothbane on 7/3/2014.
 */
public class HLVUserFeed extends Feed<User> {

    @Override
    public int getListViewRes() {
        return R.id.hlv_users;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.user_feed;
    }

    @Override
    public <S extends Manager & Singleton> Class<S> getManagerClass() {
        return (Class<S>)UserManager.class;
    }
}
