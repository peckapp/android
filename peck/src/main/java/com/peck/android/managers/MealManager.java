package com.peck.android.managers;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Food;
import com.peck.android.models.Meal;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class MealManager extends FeedManager<Meal> implements Singleton {
    private static MealManager manager = new MealManager();
    //TODO: implement this, rework this class. //private static FoodManager fManager = new foodManager();


    //these are the root lists that everything syncs from
    //data = meals
    private static ArrayList<Food> courses;

    private MealManager() { } //singleton

    public static MealManager getManager() { return manager; }

    public void linkAll(final FeedAdapter<Meal> adapter) {

    }

}
