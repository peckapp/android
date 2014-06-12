package com.peck.android.database.source;

import android.content.Context;

import com.peck.android.database.helper.MealOpenHelper;
import com.peck.android.models.Meal;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class MealDataSource extends DataSource<Meal> {


    public MealDataSource(Context context) { super(new MealOpenHelper(context)); }

    @Override
    public Meal generate() {
        return new Meal();
    }
}
