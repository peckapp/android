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
@UriPath("dining_opportunities")
@Header(singular = "dining_opportunity", plural = "dining_opportunities")
public class DiningOpportunity extends DBOperable {
    public static final transient String TYPE = "dining_opportunity_type";
    public static final transient String START_TIME = "start_time";
    public static final transient String END_TIME = "end_time";

    @Expose
    @SerializedName(TYPE)
    String type;

    @Expose
    @DBType("real")
    @SerializedName(START_TIME)
    double startTime;

    @Expose
    @DBType("real")
    @SerializedName(END_TIME)
    double endTime;

}