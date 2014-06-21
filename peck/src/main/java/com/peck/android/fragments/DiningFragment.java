package com.peck.android.fragments;

import android.os.Bundle;

import com.peck.android.R;
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.fragments.tabs.FeedTab;
import com.peck.android.managers.MealManager;
import com.peck.android.models.Meal;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class DiningFragment extends FeedTab<Meal> {
    private final static String tag = "DiningFeed";
    private final static int tabId = R.string.tb_diningfeed;
    private final static int lvId = R.id.lv_dining;


    public String tag() { return tag; }

    public int getTabTag() { //this method *is used*, don't delete
        return tabId;
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Meal e;
        for (int i = 1; i < 11; i++) {
            e = new Meal();
            e.setServerId(i);
            e.setTitle("Meal " + Integer.toString(i));
            feedManager.add(e);
        }

    }


    public DiningFragment setUpFeed() {

        if (dataSource == null) {
            dataSource = new MealDataSource(getActivity());
        }

        if (feedAdapter == null) {
            feedAdapter = new FeedAdapter<Meal>(getActivity(), dataSource);
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
