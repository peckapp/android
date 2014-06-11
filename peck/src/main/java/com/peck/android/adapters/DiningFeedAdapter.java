package com.peck.android.adapters;

import android.content.Context;

import com.peck.android.database.helper.EventOpenHelper;
import com.peck.android.database.helper.MealOpenHelper;
import com.peck.android.factories.MealFactory;
import com.peck.android.managers.EventManager;
import com.peck.android.managers.MealManager;
import com.peck.android.managers.ModelManager;
import com.peck.android.models.Event;
import com.peck.android.models.Meal;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class DiningFeedAdapter extends FeedAdapter<Meal> {

    public DiningFeedAdapter(Context context, MealFactory factory) {
        super(context, factory);
    }

}
