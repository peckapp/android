package com.peck.android.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peck.android.R;
import com.peck.android.interfaces.Singleton;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class CirclesTab extends Fragment implements BaseTab {

    private static final String tag = "Circles";
    private static final int tagId = R.string.tb_circles;
    private static final int resId = R.layout.frag_circles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO: set onclicklisteners for list items
        super.onCreate(savedInstanceState);

    }

    public int getTabTag() {
        return tagId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_circles, container, false);
    }


    //    @Override
//    public int getLayoutRes() {
//        return resId;
//    }



    @Override
    public Class<? extends Singleton> getManagerClass() {
        return null;
    }
}
