/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android.fragments;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.makeramen.RoundedImageView;
import com.peck.android.R;
import com.peck.android.interfaces.Named;
import com.peck.android.listeners.ImagePickerListener;
import com.peck.android.managers.FacebookSessionHandler;
import com.peck.android.managers.LoginManager;
import com.peck.android.network.PeckAccountAuthenticator;
import com.peck.android.views.PeckAuthButton;

import java.io.FileNotFoundException;

/**
 * Created by mammothbane on 6/10/2014.
 * a fragment for the profile page. has a member {@link com.peck.android.views.PeckAuthButton} that updates based on login state.
 */
public class ProfileTab extends Fragment implements Named {

    private static final int tabId = R.string.tb_profile;
    private static final int resId = R.layout.tab_profile;
    private UiLifecycleHelper lifecycleHelper;
    private PeckAuthButton peckAuthButton;
    private String name;
    private String imgUrl;
    private String email;
    private String id;
    private TextView tvEmail;
    private RoundedImageView profile;
    private TextView realName;


    @Override
    public String getName() {
        return "Profile";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lifecycleHelper = new UiLifecycleHelper(getActivity(), new FacebookSessionHandler.SessionStatusCallback());

        lifecycleHelper.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        if (peckAuthButton != null) peckAuthButton.update();
        super.onResume();
        lifecycleHelper.onResume();
        refresh(getView());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImagePickerListener.REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    final boolean isCamera;
                    if (data == null) {
                        isCamera = true;
                    } else {
                        final String action = data.getAction();
                        if (action == null) {
                            isCamera = false;
                        } else {
                            isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                    }

                    Uri imageUri;
                    if (isCamera) {
                        imageUri = data.getParcelableExtra(ImagePickerListener.URI);
                    } else {
                        imageUri = data == null ? null : data.getData();
                    }


                    try {
                        final Bitmap bmp = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
/*
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                try {
                                    ServerCommunicator.jsonService.patchImage("users", id, new ServerCommunicator.Jpeg("user_" + id + "_" + DateTime.now().toDateTime(DateTimeZone.forTimeZone(PeckApp.tz)).getMillis()/1000L, bmp, 2*1024*1024), JsonUtils.auth(LoginManager.getActive()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (OperationCanceledException e) {
                                    e.printStackTrace();
                                } catch (AuthenticatorException e) {
                                    e.printStackTrace();
                                } catch (LoginManager.InvalidAccountException e) {
                                    e.printStackTrace();
                                } catch (NetworkErrorException e) {
                                    e.printStackTrace();
                                } catch (RetrofitError e) {
                                    Log.e(ProfileTab.class.getSimpleName(), "retrofit encountered an error", e);
                                }

                                return null;
                            }
                        }.execute();
*/

                        Log.d(ProfileTab.class.getSimpleName(), "i have a bitmap");
                    } catch (FileNotFoundException e) { e.printStackTrace(); }

                    return;
            }
        }
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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(resId, container, false);

        id = AccountManager.get(getActivity()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.USER_ID);
        realName = ((TextView) view.findViewById(R.id.tv_realname));
        profile = ((RoundedImageView) view.findViewById(R.id.iv_event));
        tvEmail = ((TextView)view.findViewById(R.id.tv_email));

        refresh(view);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Changing profile pictures isn't supported yet.", Toast.LENGTH_LONG).show();
            }
        });
        //profile.setOnClickListener(new ImagePickerListener(this));

        LoginButton authButton = (LoginButton) view.findViewById(R.id.bt_fb_link);
        authButton.setFragment(this);

        peckAuthButton = ((PeckAuthButton)view.findViewById(R.id.bt_peck_login));
        peckAuthButton.setFragment(this);

        return view;

    }


    private void refresh(final View mView) {
        /* FIXME  -  COMMENTED FOR RELEASE
          *  not broken. needs user table to be created.
           * */

       /* new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Cursor cursor = getActivity().getContentResolver().query(DBUtils.buildLocalUri(User.class), new String[]{User.LOCAL_ID, User.FIRST_NAME, User.LAST_NAME, User.IMAGE_NAME, User.THUMBNAIL, User.SV_ID, User.EMAIL},
                        User.SV_ID + " = ?", new String[]{ id }, null);

                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    email = cursor.getString(cursor.getColumnIndex(User.EMAIL));
                    name = cursor.getString(cursor.getColumnIndex(User.FIRST_NAME)) + " " + cursor.getString(cursor.getColumnIndex(User.LAST_NAME));
                    imgUrl = cursor.getString(cursor.getColumnIndex(User.IMAGE_NAME));
                    cursor.close();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (name != null) {
                    realName.setText(name);
                    realName.setAlpha(1f);
                }
                if (email != null) {
                    tvEmail.setText(email);
                }
                if (imgUrl != null && !imgUrl.isEmpty() && !imgUrl.equals("/images/missing.png")) Picasso.with(mView.getContext())
                        .load(imgUrl).
                                into(profile, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        profile.setAlpha(1f);
                                    }

                                    @Override
                                    public void onError() {
                                        Log.e(ProfileTab.class.getSimpleName(), "Failed to load profile picture");
                                    }
                                });

            }
        }.execute();*/

    }

}
