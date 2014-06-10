package com.peck.android.fragments.tabs;

import com.peck.android.R;
import com.peck.android.factories.MealFactory;
import com.peck.android.interfaces.Feed;
import com.peck.android.models.Meal;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class DiningFeed extends Feed<Meal, MealFactory> {
    private final static String tag = "DiningFeed";
    private final static int resId = R.string.tb_diningfeed;

    public String tag() { return tag; }

    public MealFactory getFactory() {
        return new MealFactory();
    }

    public static int getTabTag() { //this method *is used*, don't delete
        return resId;
    }

}
