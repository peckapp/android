package com.peck.android.managers;

import android.os.AsyncTask;

import com.peck.android.adapters.DiningFeedAdapter;
import com.peck.android.models.Food;
import com.peck.android.models.Meal;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class MealManager {
    private static MealManager manager = new MealManager();


    private static ArrayList<Meal> meals; //these are the root lists that everything syncs from
    private static ArrayList<Food> courses;

    private MealManager() { } //force singleton

    public static MealManager getMealManager() { return manager; }

    public static void linkAll(final DiningFeedAdapter adapter) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (Meal meal : meals) {
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

    public static void linkAll() {
        linkAll(null);
    }



}
