package com.peck.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.Header;
import com.peck.android.annotations.Table;

/**
 * Created by mammothbane on 7/22/2014.
 */
@Header(singular = "athletic_event", plural = "athletic_events")
@Table("events")
public class AthleticEvent extends DBOperable {

    public final static transient String HOME_SCORE = "home_score";
    public final static transient String AWAY_SCORE = "away_score";
    public final static transient String START_DATE = "start_date";
    public final static transient String END_DATE = "end_date";

    @Expose
    @SerializedName(HOME_SCORE)
    private int homeScore;

    @Expose
    @SerializedName(AWAY_SCORE)
    private int awayScore;


}
