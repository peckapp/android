package com.peck.android.factories;

import com.peck.android.models.Meal;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class MealFactory extends GenericFactory<Meal> {

    public Meal generate() {
        return new Meal();
    }

}
