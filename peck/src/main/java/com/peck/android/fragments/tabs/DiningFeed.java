package com.peck.android.fragments.tabs;

import com.peck.android.R;
import com.peck.android.adapters.DiningFeedAdapter;
import com.peck.android.database.source.MealDataSource;
import com.peck.android.managers.MealManager;
import com.peck.android.models.Meal;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class DiningFeed extends Feed<Meal> {
    private final static String tag = "DiningFeed";
    private final static int resId = R.string.tb_diningfeed;
    private final static int lvId = R.id.lv_dining;

    public String tag() { return tag; }

    public int getTabTag() { //this method *is used*, don't delete
        return resId;
    }

    public DiningFeed setUpAdapter() {

        if (dataSource == null) {
            dataSource = new MealDataSource(getActivity());
        }

        if (feedAdapter == null) {
            feedAdapter = new DiningFeedAdapter(getActivity(), dataSource);
        }


        return this;
    }

    @Override
    protected void congfigureManager() {
        modelManager = MealManager.getManager().initialize(feedAdapter, dataSource);
    }

    public int getLayoutRes() {
        return R.layout.frag_diningfeed;
    }

    public int getListViewRes() {
        return lvId;
    }

    public Class<MealManager> getManagerClass() {
        return MealManager.class;
    }

}
