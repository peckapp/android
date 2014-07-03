package com.peck.android.models;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.R;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.Joined;
import com.peck.android.interfaces.SelfSetup;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class Event extends DBOperable implements HasFeedLayout, SelfSetup, Joined {

    @NonNull
    @Expose
    @SerializedName("start_date")
    private Date startTime = new Date(-1);

    @NonNull
    @Expose
    @SerializedName("end_date")
    private Date endTime = new Date(-1);

    @Expose
    @SerializedName("title")
    private String title = "";

    @Expose
    @SerializedName("event_description")
    private String text = "";

    @Expose
    @SerializedName("image_url")
    private String imageUrl;

    @Expose
    @SerializedName("event_url")
    private String eventUrl;

    @NonNull
    private JoinGroup<User, Event> users = new JoinGroup<User, Event>(this);

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<JoinGroup<? extends DBOperable, Event>> getJoinGroups() {
        ArrayList<JoinGroup<? extends DBOperable, Event>> joinGroups = new ArrayList<JoinGroup<? extends DBOperable, Event>>();
        joinGroups.add(users);
        return joinGroups;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    public Event setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    @Nullable
    public String getEventUrl() {
        return eventUrl;
    }

    public Event setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
        return this;
    }

    @NonNull
    public Date getStartTime() {
        return startTime;
    }

    public Event setStartTime(@NonNull Date startTime) {
        this.startTime = startTime;
        return this;
    }

    @NonNull
    public Date getEndTime() {
        return endTime;
    }

    public Event setEndTime(@NonNull Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getText() {
        return text;
    }

    public Event setText(String text) {
        this.text = text;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Event setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public int getResourceId() { //TODO: implement, create layout
        return R.layout.lvitem_event;
    }



    @Override
    public void setUp(View v, Activity activity) { //TODO: set up a layout that's passed in with the correct information
        ((TextView)v.findViewById(R.id.tv_title)).setText(title);
        ((TextView)v.findViewById(R.id.tv_text)).setText(text);
    }

}
