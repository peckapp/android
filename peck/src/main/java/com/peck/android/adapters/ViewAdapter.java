package com.peck.android.adapters;

import android.view.View;

import com.peck.android.models.DBOperable;

/**
 * Created by mammothbane on 7/11/2014.
 */

public abstract class ViewAdapter<T extends DBOperable> {

    public abstract void setUp(View view, T item);

}
