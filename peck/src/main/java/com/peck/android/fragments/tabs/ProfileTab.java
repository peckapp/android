package com.peck.android.fragments.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;

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

//        final AccessibilityManager accessibilityManager = (AccessibilityManager)getActivity().getSystemService(Context.ACCESSIBILITY_SERVICE);
//        accessibilityManager.addAccessibilityStateChangeListener(new AccessibilityManager.AccessibilityStateChangeListener() {
//            @Override
//            public void onAccessibilityStateChanged(boolean b) {
//
//            }
//        });

    }

    public int getTabTag() {
        return tabId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_profile, container, false);
    }

    @Override
    public Class<? extends Singleton> getManagerClass() {
        return ProfileManager.class;
    }
}
