package com.peck.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meetme.android.horizontallistview.HorizontalListView;
import com.peck.android.R;
import com.peck.android.database.dataspec.UserDataSpec;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.managers.ImageCacher;
import com.peck.android.views.RoundedImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class User extends DBOperable implements HasFeedLayout, SelfSetup {

    private String name = "";
    private String fbId = "";
    private int serverId;
    private String bio = "";
    private ArrayList<Circle> circles = new ArrayList<Circle>();
    private Date created = new Date(Calendar.getInstance().getTimeInMillis());
    private Date updated = new Date(Calendar.getInstance().getTimeInMillis());

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

    public String getFbId() {
        return fbId;
    }

    public User setFbId(String fbId) {
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

    public void getProfilePicture(Callback<Bitmap> callback) {
        ImageCacher.get(localId, callback);
    }

    public User strip() {
        circles = new ArrayList<Circle>();
        return this;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(UserDataSpec.COLUMN_SERVER_ID, getLocalId());
        cv.put(UserDataSpec.COLUMN_NAME, getName());
        cv.put(UserDataSpec.COLUMN_BIO, getBio());

        cv.put(UserDataSpec.COLUMN_UPDATED, dateToInt(getUpdated()));
        cv.put(UserDataSpec.COLUMN_CREATED, dateToInt(getCreated()));
        cv.put(UserDataSpec.COLUMN_FACEBOOK_ID, getFbId());

        return cv;
    }

    public User setLocalId(int id) {
        this.localId = id;
        return this;
    }
    
    @Override
    public User fromCursor(Cursor cursor) {
        cursor.moveToFirst();

        return this.setLocalId(cursor.getInt(cursor.getColumnIndex(UserDataSpec.COLUMN_LOC_ID)))
                .setServerId(cursor.getInt(cursor.getColumnIndex(UserDataSpec.COLUMN_SERVER_ID)))
                .setBio(cursor.getString(cursor.getColumnIndex(UserDataSpec.COLUMN_BIO)))
                .setName(cursor.getString(cursor.getColumnIndex(UserDataSpec.COLUMN_NAME)))
                .setCreated(new Date(cursor.getLong(cursor.getColumnIndex(UserDataSpec.COLUMN_CREATED))))
                .setUpdated(new Date(cursor.getLong(cursor.getColumnIndex(UserDataSpec.COLUMN_UPDATED))))
                .setFbId(cursor.getString(cursor.getColumnIndex(UserDataSpec.COLUMN_FACEBOOK_ID)));
    }

    @Override
    public int getResourceId() {
        return R.layout.lvitem_user;
    }

    @Override
    public void setUp(final View v) {
        //test
        //profilePicture = BitmapFactory.decodeResource(v.getResources(), R.drawable.ic_launcher);

        Log.d("user model", "setting up " + ((v instanceof HorizontalListView) ? "hlv" :
                (v instanceof LinearLayout) ? "linear layout" : "unknown"));

        if (v instanceof HorizontalListView) {
            //if this is a list item
            //((RoundedImageView)v.findViewById(R.id.riv_user)).setImageBitmap(profilePicture);
        } else if (v instanceof LinearLayout) {
            //if this is a profile page
            v.findViewById(R.id.pb_prof_loading).setVisibility(View.VISIBLE);
            getProfilePicture(new Callback<Bitmap>() {
                @Override
                public void callBack(Bitmap obj) {
                    ((RoundedImageView) v.findViewById(R.id.riv_user)).setImageBitmap(obj);
                    //todo: check this: v.findViewById(R.id.riv_user).setAlpha(1f);
                    v.findViewById(R.id.pb_prof_loading).setVisibility(View.INVISIBLE);
                }
            });
            if (name != null && !name.equals("")) {
                ((TextView) v.findViewById(R.id.tv_realname)).setText(getName());
                v.findViewById(R.id.tv_realname).setAlpha(1f);
            }
        }


    }



}
