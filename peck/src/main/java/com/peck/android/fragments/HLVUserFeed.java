package com.peck.android.fragments;

import android.support.annotation.NonNull;

import com.peck.android.BuildConfig;
import com.peck.android.R;
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.managers.DataHandler;
import com.peck.android.models.Circle;
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

        if (DataHandler.getByLocalId(Circle.class, circleId) == null && BuildConfig.DEBUG) throw new RuntimeException("circle must exist");

        users = DataHandler.getByLocalId(Circle.class, circleId).getUserIds();

        updateData();
    }

    private void updateData() {
        ArrayList<User> temp = new ArrayList<User>();
        for (int i : users) {
            temp.add(DataHandler.getByLocalId(User.class, i));
        }
        data = temp;
    }

    @Override
    public int getListViewRes() {
        return R.id.hlv_users;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.user_feed;
    }

}
