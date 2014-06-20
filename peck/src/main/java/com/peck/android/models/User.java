package com.peck.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.View;

import com.meetme.android.horizontallistview.HorizontalListView;
import com.peck.android.R;
import com.peck.android.database.helper.UserOpenHelper;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.managers.ImageCacher;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class User extends DBOperable implements HasFeedLayout, SelfSetup {

    private String name;
    private int fbId;
    private int serverId;
    private String bio;
    private ArrayList<Circle> circles;
    private Date created;
    private Date updated;

    public Date getCreated() {
        return created;
    }

    public User setCreated(Date created) {
        this.created = created;
        return this;
    }

    public Date getUpdated() {
        return updated;
    }

    public User setUpdated(Date updated) {
        this.updated = updated;
        return this;
    }

    public int getFbId() {
        return fbId;
    }

    public User setFbId(int fbId) {
        this.fbId = fbId;
        return this;
    }

    public int getServerId() {
        return serverId;
    }

    public User setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

    public String getBio() {
        return bio;
    }

    public User setBio(String bio) {
        this.bio = bio;
        return this;
    }

    public ArrayList<Circle> getCircles() {
        return circles;
    }

    public User setCircles(ArrayList<Circle> circles) {
        this.circles = circles;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public Bitmap getProfilePicture() {
        return ImageCacher.get(localId);
    }

    public User strip() {
        circles = null;
        bio = null;
        return this;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(UserOpenHelper.COLUMN_SERVER_ID, getLocalId());
        cv.put(UserOpenHelper.COLUMN_NAME, getName());
        cv.put(UserOpenHelper.COLUMN_BIO, getBio());
        cv.put(UserOpenHelper.COLUMN_LOC_ID, getLocalId());

        cv.put(UserOpenHelper.COLUMN_UPDATED, dateToInt(getUpdated()));
        cv.put(UserOpenHelper.COLUMN_CREATED, dateToInt(getCreated()));
        cv.put(UserOpenHelper.COLUMN_FACEBOOK_ID, getFbId());

        return cv;
    }

    public User setLocalId(int id) {
        this.localId = id;
        return this;
    }
    
    @Override
    public User fromCursor(Cursor cursor) {
        cursor.moveToFirst();

        return this.setLocalId(cursor.getInt(cursor.getColumnIndex(UserOpenHelper.COLUMN_LOC_ID)))
                .setServerId(cursor.getInt(cursor.getColumnIndex(UserOpenHelper.COLUMN_SERVER_ID)))
                .setBio(cursor.getString(cursor.getColumnIndex(UserOpenHelper.COLUMN_BIO)))
                .setName(cursor.getString(cursor.getColumnIndex(UserOpenHelper.COLUMN_NAME)))
                .setCreated(new Date(cursor.getLong(cursor.getColumnIndex(UserOpenHelper.COLUMN_CREATED))))
                .setUpdated(new Date(cursor.getLong(cursor.getColumnIndex(UserOpenHelper.COLUMN_UPDATED))))
                .setFbId(cursor.getInt(cursor.getColumnIndex(UserOpenHelper.COLUMN_FACEBOOK_ID)));
    }

    @Override
    public int getResourceId() {
        return R.layout.lvitem_user;
    }

    @Override
    public void setUp(View v) {
        //test
        //profilePicture = BitmapFactory.decodeResource(v.getResources(), R.drawable.ic_launcher);

        if (v instanceof HorizontalListView) {
            //if this is a list item
            //((RoundedImageView)v.findViewById(R.id.riv_user)).setImageBitmap(profilePicture);
        } else {
            //if this is a profile page

        }


    }



}
