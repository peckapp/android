package com.peck.android.models;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.makeramen.RoundedImageView;
import com.peck.android.R;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.HasImage;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.managers.ImageCacher;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class User extends DBOperable implements HasFeedLayout, SelfSetup, HasImage {

    @Expose
    @NonNull
    @SerializedName("first_name")
    private String firstName;

    @Expose
    @NonNull
    @SerializedName("last_name")
    private String lastName;

    @Expose
    @SerializedName("username")
    private String username;

    @Expose
    @SerializedName("facebook_link")
    private String fbId = "";

    @Expose
    @SerializedName("blurb")
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

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void setFullName(String string) {
        String[] temp = string.split(" ");
        //todo: support for names with multiple spaces?
        firstName = temp[0];
        lastName = temp[1];
    }

    @NonNull
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NonNull String firstName) {
        this.firstName = firstName;
    }

    @NonNull
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@NonNull String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void getProfilePicture(Callback<Bitmap> callback) {
        ImageCacher.get(this, callback);
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
                ((TextView) v.findViewById(R.id.tv_realname)).setText(getFullName());
                v.findViewById(R.id.tv_realname).setAlpha(1f);
        }


    }



}
