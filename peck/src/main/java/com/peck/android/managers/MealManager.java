package com.peck.android.managers;

import android.os.AsyncTask;

import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.source.DataSource;
import com.peck.android.database.source.FoodDataSource;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.Food;
import com.peck.android.models.Meal;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class MealManager extends ModelManager<Meal> implements Singleton {
    private static MealManager manager = new MealManager();
    //TODO: implement this, rework this class. //private static FoodManager fManager = new foodManager();


    //these are the root lists that everything syncs from
    //data = meals
    private static ArrayList<Food> courses;

    private MealManager() { } //singleton

    public static MealManager getManager() { return manager; }

    @Override
    public MealManager initialize(FeedAdapter<Meal> adapter, DataSource<Meal> dSource) {
        super.initialize(adapter, dSource);
        courses = loadFromDatabase(new FoodDataSource(adapter.getContext()));
        linkAll(adapter);
        return this;
    }

    public void linkAll(final FeedAdapter<Meal> adapter) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (Meal meal : data) {
                    for (Food course: courses) {
                        if (course.getMealId() == meal.getLocalId()) meal.link(course);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (adapter != null) adapter.notifyDataSetChanged();
            }
        }.execute();

    }

}
