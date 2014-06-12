package com.peck.android.fragments.tabs;

import android.support.v4.app.Fragment;

import com.peck.android.interfaces.HasTabTag;
import com.peck.android.interfaces.Singleton;

/**
 * Created by mammothbane on 6/12/2014.
 */
public abstract class BaseTab extends Fragment implements HasTabTag {

    public abstract int getLayoutRes();

    public abstract Class<? extends Singleton> getManagerClass();

    protected String tag() {
        return getClass().getName();
    }
}
