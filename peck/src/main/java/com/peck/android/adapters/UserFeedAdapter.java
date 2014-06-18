package com.peck.android.adapters;

import android.content.Context;

import com.peck.android.interfaces.Factory;
import com.peck.android.models.User;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class UserFeedAdapter extends FeedAdapter<User> {

    public UserFeedAdapter(Context context, Factory<User> factory) {
        super(context, factory);
    }
}
