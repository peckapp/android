package com.peck.android.adapters;

import android.content.Context;

import com.peck.android.interfaces.Factory;
import com.peck.android.models.Locale;
import com.peck.android.models.Peck;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class PeckFeedAdapter extends FeedAdapter<Peck> {

    public PeckFeedAdapter(Context context, Factory<Peck> factory) {
        super(context, factory);
    }

}
