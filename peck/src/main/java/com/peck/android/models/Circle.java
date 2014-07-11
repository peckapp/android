package com.peck.android.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.interfaces.HasImage;

import java.util.ArrayList;


/**
 * Created by mammothbane on 6/12/2014.
 */
public class Circle extends DBOperable implements HasImage {

    @Expose
    @NonNull
    @SerializedName("circle_members")
    private ArrayList<Integer> userIds = new ArrayList<Integer>();


    @Expose
    @SerializedName("circle_name")
    private String title;

    @Expose
    @SerializedName("image_link")
    private String imageUrl;

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    public Circle setImageUrl(String Url) {
        this.imageUrl = Url;
        return this;
    }

    @NonNull
    public ArrayList<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(@NonNull ArrayList<Integer> userIds) {
        this.userIds = userIds;
    }

    public String getTitle() {
        return title;
    }

    public Circle setTitle(String title) {
        this.title = title;
        return this;
    }

}
