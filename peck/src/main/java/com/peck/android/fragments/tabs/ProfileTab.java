package com.peck.android.fragments.tabs;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.internal.ImageRequest;
import com.facebook.internal.ImageResponse;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.peck.android.R;
import com.peck.android.interfaces.HasTabTag;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.ProfileManager;

import java.net.URL;
import java.util.Arrays;

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
        if (Session.getActiveSession().isOpened()) {

            Request.newMeRequest(Session.getActiveSession(),
                    new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(final GraphUser user, Response response) {
                            updateUserName(user.getName());


                            new AsyncTask<Void, Void, Void>() {
                                URL img_value = null;
                                Bitmap img = null;

                                @Override
                                protected Void doInBackground(Void... voids) {
                                    try {
                                        img_value = new URL("https://graph.facebook.com/" + user.getId() + "/picture?type=large");
                                        img = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
                                    } catch (Exception e) {
                                        Log.e(getClass().getName(), e.toString());
                                    }
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    updateProfilePicture(img);
                                }
                            }.execute();
                        }
                    }
            ).executeAsync();


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

        View view = inflater.inflate(R.layout.frag_profile, container, false);
        LoginButton authButton = (LoginButton) view.findViewById(R.id.bt_fb_link);
        authButton.setFragment(this);


        return view;
    }

    private void updateUserName(String s) {
        ((TextView)getActivity().findViewById(R.id.tv_realname)).setText(s);

        //todo: update stored username
    }

    private void updateProfilePicture(Bitmap bm) {
        ImageView iv = ((ImageView)getActivity().findViewById(R.id.iv_profile));
        iv.setImageBitmap(bm);

        //todo: update stored profile picture
    }


    @Override
    public Class<? extends Singleton> getManagerClass() {
        return ProfileManager.class;
    }
}
