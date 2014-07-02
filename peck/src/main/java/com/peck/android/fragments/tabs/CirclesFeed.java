package com.peck.android.fragments.tabs;

import android.os.Bundle;

import com.peck.android.R;
import com.peck.android.fragments.FeedTab;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.CircleManager;
import com.peck.android.managers.Manager;
import com.peck.android.models.Circle;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class CirclesFeed extends FeedTab<Circle> {

    private static final String tag = "Circles";
    private static final int tagId = R.string.tb_circles;
    private static final int resId = R.layout.tab_circlesfeed;
    private static final int lvId = R.id.lv_circles;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO: set onclicklisteners for list items
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getListViewRes() {
        return lvId;
    }

    @Override
    public int getLayoutRes() {
        return resId;
    }

    @Override
    public <S extends Manager & Singleton> Class<S> getManagerClass() {
        return (Class<S>) CircleManager.class;
    }



}
