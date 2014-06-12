package com.peck.android.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peck.android.interfaces.HasTabTag;
import com.peck.android.interfaces.Singleton;

/**
 * Created by mammothbane on 6/12/2014.
 */
public abstract class BaseTab extends Fragment implements HasTabTag {

    public abstract int getLayoutRes();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(getLayoutRes(), container, false);
    }

    public abstract Class<? extends Singleton> getManagerClass();

    protected String tag() {
        return getClass().getName();
    }
}
