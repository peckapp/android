/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

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
@UriPath("dining_places")
@Header(singular = "dining_place", plural = "dining_places")
public class DiningPlace extends DBOperable {
    public static final transient String NAME = "name";
    public static final transient String DETAILS_LINK = "details_link";
    public static final transient String LONGITUDE = "gps_longitude";
    public static final transient String LATITUDE = "gps_latitude";
    public static final transient String RANGE = "range";
    public static final transient String HOURS = "hours";

    @Expose
    @SerializedName(HOURS)
    String hours;

    @Expose
    @SerializedName(NAME)
    String name;

    @Expose
    @SerializedName(DETAILS_LINK)
    String detailsLink;

    @Expose
    @DBType("real")
    @SerializedName(LONGITUDE)
    double longitude;

    @Expose
    @DBType("real")
    @SerializedName(LATITUDE)
    double latitude;


    @Expose
    @DBType("real")
    @SerializedName(RANGE)
    double range;
}
