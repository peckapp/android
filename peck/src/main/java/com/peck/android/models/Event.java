package com.peck.android.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.DBType;
import com.peck.android.annotations.Table;

import java.util.ArrayList;

/**
 * Created by mammothbane on 7/22/2014.
 */

@Table("events")
public class Event extends DBOperable {
    public static final transient String START_DATE = "start_date";
    public static final transient String END_DATE = "end_date";
    public static final transient String TITLE = "title";
    public static final transient String TEXT = "event_description";
    public static final transient String IMAGE_URL = "image_url";
    public static final transient String EVENT_URL = "event_url";
    public static final transient String USER_IDS = "user_ids";

    public static final transient int ATHLETIC_EVENT = 1;
    public static final transient int SIMPLE_EVENT = 0;

    int type;


    /* fields for both athletic and simple events */
    @Expose
    @DBType("real")
    @SerializedName(START_DATE)
    double startTime;

    @Expose
    @DBType("real")
    @SerializedName(END_DATE)
    double endTime;

    @Expose
    @SerializedName(TITLE)
    String title = "";

    @Expose
    @SerializedName(TEXT)
    String text = "";

    @Expose
    @SerializedName(IMAGE_URL)
    String imageUrl;

    @Expose
    @SerializedName(EVENT_URL)
    String eventUrl;

    @NonNull
    @Expose
    @SerializedName(USER_IDS)
    ArrayList<Integer> users = new ArrayList<Integer>();


    /* athletic event fields: */
    public final static transient String HOME_SCORE = "home_score";
    public final static transient String AWAY_SCORE = "away_score";

    @Expose
    @SerializedName(HOME_SCORE)
    int homeScore;

    @Expose
    @SerializedName(AWAY_SCORE)
    int awayScore;

}

