package com.peck.android.adapters;

import android.content.Context;

import com.peck.android.interfaces.Factory;
import com.peck.android.models.Circle;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class CirclesFeedAdapter extends FeedAdapter<Circle> {

    public CirclesFeedAdapter(Context context, Factory<Circle> factory) {
        super(context, factory);
    }
}
