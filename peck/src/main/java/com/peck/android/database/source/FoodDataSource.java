package com.peck.android.database.source;

import android.content.Context;

import com.peck.android.database.helper.FoodOpenHelper;
import com.peck.android.models.Food;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class FoodDataSource extends DataSource<Food> {

    public FoodDataSource(Context context) {

        super(new FoodOpenHelper(context));

    }

}
