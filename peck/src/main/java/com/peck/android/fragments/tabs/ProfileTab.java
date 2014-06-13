package com.peck.android.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.peck.android.R;
import com.peck.android.interfaces.HasTabTag;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.ProfileManager;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class ProfileTab extends Fragment implements BaseTab {

    private static final int tabId = R.string.tb_profile;
    private static final int resId = R.layout.frag_profile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO: set onclicklisteners for list items
        super.onCreate(savedInstanceState);

    }

    public int getTabTag() {
        return tabId;
    }

    @Override
    public int getLayoutRes() {
        return resId;
    }

    @Override
    public Class<? extends Singleton> getManagerClass() {
        return ProfileManager.class;
    }
}
