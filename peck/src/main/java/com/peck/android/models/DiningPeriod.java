package com.peck.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.DBType;
import com.peck.android.annotations.Header;
import com.peck.android.annotations.NoMod;
import com.peck.android.annotations.UriPath;

/**
 * Created by mammothbane on 7/28/2014.
 */

@NoMod
@Header(plural = "dining_periods", singular = "dining_period")
@UriPath("dining_periods")
public class DiningPeriod extends DBOperable {
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
    @DBType("real")
    @SerializedName(DINING_START_TIME)
    long startTime;

    @Expose
    @DBType("real")
    @SerializedName(DINING_END_TIME)
    double endTime;

    @Expose
    @DBType("integer")
    @SerializedName(DINING_OPPORTUNITY_ID)
    long opportunityId;

    @Expose
    @DBType("integer")
    @SerializedName(DINING_PLACE_ID)
    long placeId;

}
