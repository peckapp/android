package com.peck.android.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.Header;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mammothbane on 5/28/2014.
 */
@Header(plural = "simple_events", singular = "simple_event")
public class Event extends DBOperable {
    public static final transient String START_DATE = "start_date";
    public static final transient String END_DATE = "end_date";
    public static final transient String TITLE = "title";
    public static final transient String TEXT = "event_description";
    public static final transient String IMAGE_URL = "image_url";
    public static final transient String EVENT_URL = "event_url";
    public static final transient String USER_IDS = "user_ids";

    @NonNull
    @Expose
    @SerializedName(START_DATE)
    private Date startTime = new Date(-1);

    @NonNull
    @Expose
    @SerializedName(END_DATE)
    private Date endTime = new Date(-1);

    @Expose
    @SerializedName(TITLE)
    private String title = "";

    @Expose
    @SerializedName(TEXT)
    private String text = "";

    @Expose
    @SerializedName(IMAGE_URL)
    private String imageUrl;

    @Expose
    @SerializedName(EVENT_URL)
    private String eventUrl;

    @NonNull
    @Expose
    @SerializedName(USER_IDS)
    private ArrayList<Integer> users = new ArrayList<Integer>();

}
