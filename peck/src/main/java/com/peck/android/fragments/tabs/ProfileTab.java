package com.peck.android.fragments.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.peck.android.R;
import com.peck.android.activities.LoginActivity;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.FacebookSessionManager;
import com.peck.android.managers.PeckSessionManager;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class ProfileTab extends BaseTab {

    private static final int tabId = R.string.tb_profile;
    private static final int resId = R.layout.tab_profile;
    private UiLifecycleHelper lifecycleHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO: set onclicklisteners for list items
        super.onCreate(savedInstanceState);

        lifecycleHelper = new UiLifecycleHelper(getActivity(), new FacebookSessionManager.SessionStatusCallback(new Callback() {
            @Override
            public void callBack(Object obj) {
                PeckSessionManager.getUser().setUp(getActivity().findViewById(R.id.ll_profile));
            }
        }));

        lifecycleHelper.onCreate(savedInstanceState);

    }

    //todo: text resize method if name's too long

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

        View view = inflater.inflate(resId, container, false);
        LoginButton authButton = (LoginButton) view.findViewById(R.id.bt_fb_link);
        authButton.setFragment(this);

        view.findViewById(R.id.bt_peck_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        PeckSessionManager.getUser().setUp(view.findViewById(R.id.ll_profile));

        return view;
    }


    @Override
    public Class<? extends Singleton> getManagerClass() {
        return null;
    }
}
