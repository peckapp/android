package com.peck.android.database.source;

import android.app.Activity;

import com.peck.android.database.helper.CirclesOpenHelper;
import com.peck.android.models.Circle;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class CirclesDataSource extends DataSource<Circle> {


    public CirclesDataSource(Activity activity) {
        super(new CirclesOpenHelper(activity));
    }

    @Override
    public Circle generate() {
        return new Circle();
    }


}
