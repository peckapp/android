package com.peck.android.models;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.R;
import com.peck.android.fragments.HLVUserFeed;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.HasImage;
import com.peck.android.interfaces.Joined;
import com.peck.android.interfaces.SelfSetup;

import java.util.ArrayList;

import it.sephiroth.android.library.widget.HListView;

/**
 * Created by mammothbane on 6/12/2014.
 */
public class Circle extends DBOperable implements SelfSetup, HasFeedLayout, HasImage, Joined {

    @NonNull
    private JoinGroup<User, Event> users = new JoinGroup<User, Event>(this);


    @Expose
    @SerializedName("circle_name")
    private String title;

    @Expose
    @SerializedName("image_link")
    private String imageUrl;

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<JoinGroup<? extends DBOperable, Event>> getJoinGroups() {
        ArrayList<JoinGroup<? extends DBOperable, Event>> joinGroups = new ArrayList<JoinGroup<? extends DBOperable, Event>>();
        joinGroups.add(users);
        return joinGroups;
    }


    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    public Circle setImageUrl(String Url) {
        this.imageUrl = Url;
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
    public void setUp(final View v, Activity activity) {

        ((TextView)v.findViewById(R.id.tv_title)).setText(getTitle());
        HLVUserFeed userFeed = new HLVUserFeed();
        userFeed.setUp((HListView)v.findViewById(R.id.hlv_users));
    }


    @Override
    public int getResourceId() {
        return R.layout.lvitem_circle;
    }


}
