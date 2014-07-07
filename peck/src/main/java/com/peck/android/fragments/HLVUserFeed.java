package com.peck.android.fragments;

import android.support.annotation.NonNull;

import com.peck.android.BuildConfig;
import com.peck.android.R;
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.CircleManager;
import com.peck.android.managers.Manager;
import com.peck.android.managers.UserManager;
import com.peck.android.models.User;

import java.util.ArrayList;

import it.sephiroth.android.library.widget.HListView;

/**
 * Created by mammothbane on 7/3/2014.
 */
public class HLVUserFeed extends Feed<User> {

    @NonNull
    ArrayList<Integer> users;

    {
        feedAdapter = new FeedAdapter<User>(R.layout.hlvitem_user, this);
    }

    public void setUp(HListView hlv, int circleId) {
        hlv.setAdapter(feedAdapter);
        congfigureManager();

        if (CircleManager.getManager().getByLocalId(circleId) == null && BuildConfig.DEBUG) throw new RuntimeException("circle must exist");

        users = CircleManager.getManager().getByLocalId(circleId).getUserIds();

        updateData();
    }

    private void updateData() {
        ArrayList<User> temp = new ArrayList<User>();
        for (int i : users) {
            temp.add(UserManager.getManager().getByLocalId(i));
        }
        data = temp;
    }

    @Override
    public void notifyDatasetChanged() {
        updateData();
        super.notifyDatasetChanged();
    }

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
