package com.peck.android.fragments.tabs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.peck.android.R;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.FacebookSessionManager;
import com.peck.android.managers.PeckSessionManager;
import com.peck.android.managers.ProfileManager;
import com.peck.android.views.RoundedImageView;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class ProfileTab extends BaseTab {

    private static final int tabId = R.string.tb_profile;
    private static final int resId = R.layout.tab_profile;
    private UiLifecycleHelper lifecycleHelper;
    private TextView tv;
    private Bitmap picture;
    private int profDimens;
    private RoundedImageView riv;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO: set onclicklisteners for list items
        super.onCreate(savedInstanceState);

        profDimens = getResources().getDimensionPixelSize(R.dimen.prof_picture_bound);

        lifecycleHelper = new UiLifecycleHelper(getActivity(), new FacebookSessionManager.SessionStatusCallback(new Callback() {
            @Override
            public void callBack(Object obj) {
                tv.setText(PeckSessionManager.getUserName());
                tv.setAlpha(1f);
                riv.setImageBitmap(PeckSessionManager.getProfilePicture());
                riv.setAlpha(1f);
            }
        }));

        lifecycleHelper.onCreate(savedInstanceState);

    }

    //todo: text resize method if name's too long

    @Override
    public void onResume() {
        super.onResume();
        lifecycleHelper.onResume();
        if (Session.getActiveSession().isOpened()) {




        }


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
        //todo: set the user's profile picture to the one we've stored

        View view = inflater.inflate(resId, container, false);
        LoginButton authButton = (LoginButton) view.findViewById(R.id.bt_fb_link);
        authButton.setFragment(this);

        riv = (RoundedImageView)view.findViewById(R.id.riv);
        tv = (TextView)view.findViewById(R.id.tv_realname);
        tv.setText(FacebookSessionManager.getUserName());

        return view;
    }

    private void updateUserName(String s) {
        tv.setText(s);

        //todo: update stored username
    }

    private void updateProfilePicture(Bitmap bm) {

        //todo: update stored profile picture
    }


    @Override
    public Class<? extends Singleton> getManagerClass() {
        return ProfileManager.class;
    }
}
