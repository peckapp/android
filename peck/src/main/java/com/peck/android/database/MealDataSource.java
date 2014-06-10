package com.peck.android.database;

import android.content.Context;

import com.peck.android.factories.MealFactory;
import com.peck.android.models.Meal;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class MealDataSource extends DataSource<Meal, MealOpenHelper> {


    public MealDataSource(Context context) { super(new MealOpenHelper(context)); }


}
