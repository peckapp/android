package com.peck.android.fragments.tabs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
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
    private UiLifecycleHelper lifecycleHelper;

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(getClass().getName(), "Logged in...");
        } else if (state.isClosed()) {
            Log.i(getClass().getName(), "Logged out...");
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO: set onclicklisteners for list items
        super.onCreate(savedInstanceState);

        lifecycleHelper = new UiLifecycleHelper(getActivity(), callback);
        lifecycleHelper.onCreate(savedInstanceState);

//        final AccessibilityManager accessibilityManager = (AccessibilityManager)getActivity().getSystemService(Context.ACCESSIBILITY_SERVICE);
//        accessibilityManager.addAccessibilityStateChangeListener(new AccessibilityManager.AccessibilityStateChangeListener() {
//            @Override
//            public void onAccessibilityStateChanged(boolean b) {
//
//            }
//        });

    }

    @Override
    public void onResume() {
        super.onResume();
        lifecycleHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        lifecycleHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        lifecycleHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycleHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        lifecycleHelper.onSaveInstanceState(outState);
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
