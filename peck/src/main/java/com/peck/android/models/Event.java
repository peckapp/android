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
    public static final transient int SIMPLE_EVENT = 0;
    public static final transient int ATHLETIC_EVENT = 1;
    public static final transient int DINING_OPPORTUNITY = 2;
    public static final transient int ANNOUNCEMENT = 3;

    @SerializedName("type")
    @DBType("integer")
    int type;

    /* shared fields */
    //simple events and announcements
    @Expose
    @SerializedName(TITLE)
    String title;

    //simple events and announcements
    @Expose
    @SerializedName(IMAGE_URL)
    String imageUrl;


    /* simple event fields */
    public static final transient String START_DATE = "start_date";
    public static final transient String END_DATE = "end_date";
    public static final transient String TITLE = "title";
    public static final transient String TEXT = "event_description";
    public static final transient String IMAGE_URL = "image_file_name";
    public static final transient String EVENT_URL = "event_url";
    public static final transient String USER_IDS = "user_ids";
    public static final transient String TYPE = "type";

    @Expose
    @DBType("real")
    @SerializedName(START_DATE)
    double startDate;

    @Expose
    @DBType("real")
    @SerializedName(END_DATE)
    double endDate;

    @Expose
    @SerializedName(TEXT)
    String text;

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
    @DBType("real")
    @SerializedName(ATHLETIC_HOME_SCORE)
    double homeScore;

    @Expose
    @DBType("real")
    @SerializedName(ATHLETIC_AWAY_SCORE)
    double awayScore;

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
    @DBType("real")
    @SerializedName(ATHLETIC_DATE_AND_TIME)
    double dateTime;


    /* dining opportunity fields */
    public static final transient String DINING_OP_TYPE = "dining_opportunity_type";
    public static final transient String DINING_START_TIME = "start_time";
    public static final transient String DINING_END_TIME = "end_time";

    @Expose
    @SerializedName(DINING_OP_TYPE)
    String diningType;

    @Expose
    @DBType("real")
    @SerializedName(DINING_START_TIME)
    double startTime;

    @Expose
    @DBType("real")
    @SerializedName(DINING_END_TIME)
    double endTime;

    /* announcement fields */
    public static final transient String ANNOUNCEMENT_TITLE = "title";
    public static final transient String ANNOUNCEMENT_TEXT = "announcement_description";
    public static final transient String ANNOUNCEMENT_USER_ID = "user_id";
    public static final transient String ANNOUNCEMENT_DEPARTMENT_ID = "department_id";
    public static final transient String ANNOUNCEMENT_CLUB_ID = "club_id";
    public static final transient String ANNOUNCEMENT_CIRCLE_ID = "circle_id";
    public static final transient String ANNOUNCEMENT_PUBLIC = "public";
    public static final transient String ANNOUNCEMENT_COMMENT_COUNT = "comment_count";
    public static final transient String ANNOUNCEMENT_IMAGE_FILE_NAME = "image_file_name";
    public static final transient String ANNOUNCEMENT_IMAGE_CONTENT_TYPE = "image_content_type";
    public static final transient String ANNOUNCEMENT_IMAGE_FILE_SIZE = "image_file_size";
    public static final transient String ANNOUNCEMENT_IMAGE_UPDATED_AT = "image_updated_at";

    @Expose
    @SerializedName(ANNOUNCEMENT_TEXT)
    String announceText;

    @Expose
    @DBType("integer")
    @SerializedName(ANNOUNCEMENT_USER_ID)
    long announceUserId;

    @Expose
    @DBType("integer")
    @SerializedName(ANNOUNCEMENT_DEPARTMENT_ID)
    long announceDepartmentId;

    @Expose
    @DBType("integer")
    @SerializedName(ANNOUNCEMENT_CLUB_ID)
    long announceClubId;

    @Expose
    @DBType("integer")
    @SerializedName(ANNOUNCEMENT_CIRCLE_ID)
    long announceCircleId;

    @Expose
    @DBType("boolean")
    @SerializedName(ANNOUNCEMENT_PUBLIC)
    boolean announcePublic;

    @Expose
    @DBType("integer")
    @SerializedName(ANNOUNCEMENT_COMMENT_COUNT)
    int announceCommentCount;

    @Expose
    @SerializedName(ANNOUNCEMENT_IMAGE_CONTENT_TYPE)
    String announceImageContentType;

    @Expose
    @DBType("integer")
    @SerializedName(ANNOUNCEMENT_IMAGE_FILE_SIZE)
    int announceImageFileSize;

    @Expose
    @DBType("real")
    @SerializedName(ANNOUNCEMENT_IMAGE_UPDATED_AT)
    double announceImageUpdated;

}

