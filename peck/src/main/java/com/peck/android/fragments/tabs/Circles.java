package com.peck.android.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.peck.android.R;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class Circles extends Fragment {

    private static final String tag = "Circles";
    private static final int resId = R.string.tb_circles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO: set onclicklisteners for list items
        super.onCreate(savedInstanceState);

    }

    public static int getTabTag() {
        return resId;
    }

}
