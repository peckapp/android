package com.peck.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.meetme.android.horizontallistview.HorizontalListView;
import com.peck.android.R;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.views.RoundedImageView;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class User extends DBOperable implements HasFeedLayout, SelfSetup {

    private Bitmap profilePicture;

    @Override
    public ContentValues toContentValues() {
        return null;
    }

    @Override
    public DBOperable fromCursor(Cursor cursor) {
        return null;
    }

    @Override
    public int getResourceId() {
        return R.layout.lvitem_user;
    }

    @Override
    public void setUp(View v) {
        //test
        profilePicture = BitmapFactory.decodeResource(v.getResources(), R.drawable.ic_launcher);

        if (v instanceof HorizontalListView) {
            //if this is a list item
            ((RoundedImageView)v.findViewById(R.id.riv_user)).setImageBitmap(profilePicture);
        } else {
            //if this is a profile page

        }


    }



}
