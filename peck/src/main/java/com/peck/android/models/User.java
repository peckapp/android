package com.peck.android.models;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.makeramen.RoundedImageView;
import com.peck.android.R;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.HasWebImage;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.managers.ImageCacher;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class User extends DBOperable implements HasFeedLayout, SelfSetup, HasWebImage {

    @Expose
    @NonNull
    private String name = "";

    @Expose
    private String fbId = "";

    @Expose
    private String bio = "";

    @Expose
    private String profileUrl = "";


    public String getFbId() {
        return fbId;
    }

    public User setFbId(String fbId) {
        this.fbId = fbId;
        return this;
    }

    @Override
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

    public String getImageUrl() {
        return profileUrl;
    }

    public User setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
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
        ImageCacher.get(this, callback);
    }


    public User setLocalId(int id) {
        this.localId = id;
        return this;
    }

    @Override
    public int getResourceId() {
        return R.layout.lvitem_user;
    }

    @Override
    public void setUp(final View v) {

        Log.d("User " + getLocalId(), "Setting up " + ((v instanceof RelativeLayout) ? "circles user item." :
                (v instanceof LinearLayout) ? "profile." : "unknown view."));

        if (v instanceof RelativeLayout) { //todo: fix this, this is a stupid way to make this work
            final RoundedImageView roundedImageView = (RoundedImageView)v.findViewById(R.id.riv_user);
            roundedImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("User " + getLocalId(), "I was clicked");
                    //todo: open the user's profile page
                }
            });
            getProfilePicture(new Callback<Bitmap>() {
                @Override
                public void callBack(Bitmap obj) {
                    roundedImageView.setImageBitmap(obj);
                }
            });
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
