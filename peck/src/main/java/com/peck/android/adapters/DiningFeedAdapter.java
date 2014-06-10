package com.peck.android.adapters;

import android.content.Context;

import com.peck.android.factories.EventFactory;
import com.peck.android.factories.MealFactory;
import com.peck.android.models.Meal;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class DiningFeedAdapter extends FeedAdapter<Meal> {

    public DiningFeedAdapter(Context context, MealFactory factory) {
        super(context, factory);
    }

}
