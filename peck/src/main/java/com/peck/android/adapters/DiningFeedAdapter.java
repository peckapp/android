package com.peck.android.adapters;

import android.content.Context;

import com.peck.android.interfaces.Factory;
import com.peck.android.models.Meal;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class DiningFeedAdapter extends FeedAdapter<Meal> {

    public DiningFeedAdapter(Context context, Factory<Meal> factory) {
        super(context, factory);
    }

}
