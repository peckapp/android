package com.peck.android.fragments.tabs;

import com.peck.android.R;
import com.peck.android.adapters.DiningFeedAdapter;
import com.peck.android.database.source.MealDataSource;
import com.peck.android.database.helper.MealOpenHelper;
import com.peck.android.factories.MealFactory;
import com.peck.android.models.Meal;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class DiningFeed extends Feed<Meal, MealFactory, MealOpenHelper> {
    private final static String tag = "DiningFeed";
    private final static int resId = R.string.tb_diningfeed;

    public String tag() { return tag; }

    public MealFactory getFactory() {
        return new MealFactory();
    }

    public int getTabTag() { //this method *is used*, don't delete
        return resId;
    }

    public DiningFeed setUpAdapter() {
        if (feedAdapter == null) {
            feedAdapter = new DiningFeedAdapter(getActivity(), getFactory());
        }

        if (dataSource == null) {
            dataSource = new MealDataSource(getActivity());
        }

        return this;
    }

    public int getLayoutRes() {
        return R.layout.frag_diningfeed;
    }

}
