package com.peck.android.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peck.android.R;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.PostManager;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class NewPostTab extends Fragment implements BaseTab {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_newpost, container, false);
    }


    @Override
    public Class<? extends Singleton> getManagerClass() {
        return PostManager.class;
    }

    @Override
    public int getTabTag() {
        return R.string.tb_newpost;
    }


}
