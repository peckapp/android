package com.peck.android.fragments;

import com.peck.android.R;
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.DataSource;
import com.peck.android.database.dataspec.MealDataSpec;
import com.peck.android.fragments.tabs.FeedTab;
import com.peck.android.managers.MealManager;
import com.peck.android.models.Meal;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class DiningFeed extends FeedTab<Meal> {
    private final static String tag = "DiningFeed";
    private final static int tabId = R.string.tb_diningfeed;
    private final static int lvId = R.id.lv_dining;


    public String tag() { return tag; }

    public DiningFeed setUpFeed() {

        if (dataSource == null) {
            dataSource = new DataSource<Meal>(MealDataSpec.getInstance());
        }

        if (feedAdapter == null) {
            feedAdapter = new FeedAdapter<Meal>(dataSource.generate().getResourceId());
        }


        return this;
    }

    @Override
    protected void congfigureManager() {
        feedManager = MealManager.getManager().initialize(feedAdapter, dataSource);
    }

    public int getLayoutRes() {
        return R.layout.tab_diningfeed;
    }

    public int getListViewRes() {
        return lvId;
    }

    public Class<MealManager> getManagerClass() {
        return MealManager.class;
    }

}
