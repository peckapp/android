package com.peck.android.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peck.android.interfaces.HasManager;
import com.peck.android.interfaces.HasTabTag;
import com.peck.android.interfaces.Singleton;

/**
 * Created by mammothbane on 6/12/2014.
 */
public interface BaseTab extends HasTabTag, HasManager{

    public abstract int getLayoutRes();


}
