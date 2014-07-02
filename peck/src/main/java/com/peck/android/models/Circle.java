package com.peck.android.models;

import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.R;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.HasImage;
import com.peck.android.interfaces.SelfSetup;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/12/2014.
 */
public class Circle extends DBOperable implements SelfSetup, HasFeedLayout, HasImage {

    private ArrayList<Integer> users = new ArrayList<Integer>();

    @Expose
    @SerializedName("circle_name")
    private String title = "";

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

    public ArrayList<Integer> getUsers() {
        return users;
    }

    public Circle setUsers(ArrayList<Integer> users) {
        this.users = users;
        return this;
    }


    public String getTitle() {
        return title;
    }

    public Circle setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setUp(final View v) {

        ((TextView)v.findViewById(R.id.tv_title)).setText(title);

        //todo: add a fragment


    }


    @Override
    public int getResourceId() {
        return R.layout.lvitem_circle;
    }


}
