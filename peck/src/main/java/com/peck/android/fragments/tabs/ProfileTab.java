package com.peck.android.fragments.tabs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.peck.android.R;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.ProfileManager;
import com.peck.android.views.RoundedImageView;

import java.net.URL;

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

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(getClass().getName(), "Logged in...");
            Request.newMeRequest(Session.getActiveSession(),
                    new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(final GraphUser user, Response response) {
                            new AsyncTask<String, Void, Void>() {
                                @Override
                                protected Void doInBackground(String... strings) {
                                    try {
                                        picture = BitmapFactory.decodeStream(new URL("https://graph.facebook.com/" + strings[0] + "/picture?width=" + profDimens + "&height=" + profDimens).openConnection().getInputStream());
                                    } catch (Exception e) {
                                        Log.e(getClass().getName(), e.toString());
                                    }
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    tv.setText(user.getName());
                                    tv.setAlpha(1f);
                                    riv.setImageBitmap(picture);
                                    riv.setAlpha(1f);
                                }
                            }.execute(user.getId());

                        }
                    }
            ).executeAsync();




        } else if (state.isClosed()) {
            Log.i(getClass().getName(), "Logged out...");

            //TODO: revert non-peck facebook information

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

        profDimens = getResources().getDimensionPixelSize(R.dimen.prof_picture_bound);

        lifecycleHelper = new UiLifecycleHelper(getActivity(), callback);
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
