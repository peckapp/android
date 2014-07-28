package com.peck.android.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.DBType;
import com.peck.android.annotations.UriPath;

import java.util.ArrayList;

/**
 * Created by mammothbane on 7/22/2014.
 */

@UriPath("events")
public class Event extends DBOperable {
    public static final transient String START_DATE = "start_date";
    public static final transient String END_DATE = "end_date";
    public static final transient String TITLE = "title";
    public static final transient String TEXT = "event_description";
    public static final transient String IMAGE_URL = "image_file_name";
    public static final transient String EVENT_URL = "event_url";
    public static final transient String USER_IDS = "user_ids";
    public static final transient String TYPE = "type";

    public static final transient int SIMPLE_EVENT = 0;
    public static final transient int ATHLETIC_EVENT = 1;
    public static final transient int DINING_PERIOD = 2;

    @SerializedName("type")
    @DBType("integer")
    int type;

    /* simple event fields */
    @Expose
    @DBType("real")
    @SerializedName(START_DATE)
    double startDate;

    @Expose
    @DBType("real")
    @SerializedName(END_DATE)
    double endDate;

    @Expose
    @SerializedName(TITLE)
    String title;

    @Expose
    @SerializedName(TEXT)
    String text;

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

    /* athletic event fields */
    public final static transient String ATHLETIC_HOME_SCORE = "team_score";
    public final static transient String ATHLETIC_AWAY_SCORE = "opponent_score";
    public final static transient String ATHLETIC_TEAM_ID = "athletic_team_id";
    public final static transient String ATHLETIC_OPPONENT = "opponent";
    public final static transient String ATHLETIC_HOME_AWAY = "home_or_away";
    public final static transient String ATHLETIC_LOCATION = "location";
    public final static transient String ATHLETIC_RESULT = "result";
    public final static transient String ATHLETIC_NOTE = "note";
    public final static transient String ATHLETIC_DATE_AND_TIME = "date_and_time";

    @Expose
    @DBType("integer")
    @SerializedName(ATHLETIC_HOME_SCORE)
    int homeScore;

    @Expose
    @DBType("integer")
    @SerializedName(ATHLETIC_AWAY_SCORE)
    int awayScore;

    @Expose
    @DBType("integer")
    @SerializedName(ATHLETIC_TEAM_ID)
    long teamId;

    @Expose
    @SerializedName(ATHLETIC_OPPONENT)
    String opponent;

    @Expose
    @SerializedName(ATHLETIC_HOME_AWAY)
    String homeAway;

    @Expose
    @SerializedName(ATHLETIC_LOCATION)
    String location;

    @Expose
    @SerializedName(ATHLETIC_RESULT)
    String result;

    @Expose
    @SerializedName(ATHLETIC_NOTE)
    String note;

    @Expose
    @DBType("integer")
    @SerializedName(ATHLETIC_DATE_AND_TIME)
    long dateTime;


    /* dining period fields */
    public final static transient String DINING_DAY_OF_WEEK = "day_of_week";
    public final static transient String DINING_START_TIME = "start_time";
    public final static transient String DINING_END_TIME = "end_time";
    public final static transient String DINING_OPPORTUNITY_ID = "dining_opportunity_id";
    public final static transient String DINING_PLACE_ID = "dining_place_id";

    @Expose
    @DBType("integer")
    @SerializedName(DINING_DAY_OF_WEEK)
    int dayOfWeek;

    @Expose
    @DBType("integer")
    @SerializedName(DINING_START_TIME)
    long startTime;

    @Expose
    @DBType("integer")
    @SerializedName(DINING_END_TIME)
    long endTime;

    @Expose
    @DBType("integer")
    @SerializedName(DINING_OPPORTUNITY_ID)
    long opportunityId;

    @Expose
    @DBType("integer")
    @SerializedName(DINING_PLACE_ID)
    long placeId;

}

