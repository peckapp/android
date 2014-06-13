package com.peck.android.adapters;

import android.content.Context;

import com.peck.android.interfaces.Factory;
import com.peck.android.models.Locale;

/**
 * Created by mammothbane on 6/13/2014.
 */
public class LocaleSelectAdapter extends FeedAdapter<Locale> {

    public LocaleSelectAdapter(Context context, Factory<Locale> factory) {
        super(context, factory);
    }
}
