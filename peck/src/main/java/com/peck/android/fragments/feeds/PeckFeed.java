/*
  * Copyright (c) 2014 Peck LLC.
  * All rights reserved.
  */

package com.peck.android.fragments.feeds;

import android.accounts.AccountManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;

import com.makeramen.RoundedImageView;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.database.DBUtils;
import com.peck.android.fragments.Feed;
import com.peck.android.interfaces.Named;
import com.peck.android.managers.LoginManager;
import com.peck.android.models.Peck;
import com.peck.android.models.User;
import com.peck.android.network.PeckAccountAuthenticator;
import com.squareup.picasso.Picasso;

/**
  * Created by mammothbane on 8/14/2014.
  */
public class PeckFeed extends Feed implements Named {

    {
        binds_from = new String[] {};
        binds_to = new int[] {};
        listItemRes = R.layout.lvitem_peck;
        loaderBundle = new Bundle();
        loaderBundle.putParcelable(LOADER_URI, DBUtils.buildLocalUri(Peck.class));
        loaderBundle.putStringArray(LOADER_PROJECTION, new String[]{ Peck.LOCAL_ID, Peck.TEXT, Peck.INVITED_BY, Peck.USER_ID, Peck.UPDATED_AT});
        loaderBundle.putString(LOADER_SELECTION, Peck.USER_ID + " = ?");
        loaderBundle.putStringArray(LOADER_SELECT_ARGS, new String[]{AccountManager.get(PeckApp.getContext()).getUserData(LoginManager.getActive(), PeckAccountAuthenticator.USER_ID)});
        loaderBundle.putString(LOADER_SORT_ORDER, Peck.UPDATED_AT);
    }


    @Override
    public String getName() {
        return "Pecks";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinder =new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue ( final View view, Cursor cursor,int i){
                switch (view.getId()) {
                    case R.id.iv_def:
                        final long userId = cursor.getLong(i);
                        if (userId > 0) {
                            new AsyncTask<Void, Void, String>() {
                                @Override
                                protected String doInBackground(Void... voids) {
                                    String url = null;
                                    Cursor user = getActivity().getContentResolver().query(DBUtils.buildLocalUri(User.class), new String[]{User.LOCAL_ID, User.SV_ID, User.THUMBNAIL, User.IMAGE_NAME},
                                            User.SV_ID + "= ?", new String[]{Long.toString(userId)}, null);
                                    if (user.getCount() > 0) {
                                        user.moveToFirst();
                                        url = user.getString(user.getColumnIndex(User.IMAGE_NAME));
                                    }
                                    user.close();
                                    return url;
                                }

                                @Override
                                protected void onPostExecute(String url) {
                                    if (url != null) {
                                        Picasso.with(getActivity())
                                                .load(url)
                                                .centerCrop()
                                                .fit()
                                                .into(((RoundedImageView) view));
                                    }

                                }
                            }.execute();
                        }
                        return true;
                    default:
                        return false;
                }
            }
        };
    }
}
