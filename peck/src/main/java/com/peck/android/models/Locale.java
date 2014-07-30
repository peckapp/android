package com.peck.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.Header;
import com.peck.android.annotations.UriPath;

/**
 * Created by mammothbane on 6/11/2014.
 */
@Header(plural = "institutions", singular = "institution")
@UriPath("locales")
public class Locale extends DBOperable {

    public static final transient String NAME = "name";
    public static final transient String ADDRESS = "street_address";
    public static final transient String STATE = "state";
    public static final transient String COUNTRY = "country";
    public static final transient String RANGE = "range";
    public static final transient String LONGITUDE = "longitude";
    public static final transient String LATITUDE = "latitude";

    @Expose
    @SerializedName(NAME)
    private String name;

    @Expose
    @SerializedName(ADDRESS)
    private String streetAddress;

    @Expose
    @SerializedName(STATE)
    private String state;

    @Expose
    @SerializedName(COUNTRY)
    private String country;

    @Expose
    @SerializedName(RANGE)
    private double range;

    @Expose
    @SerializedName(LONGITUDE)
    private double longitude;

    @Expose
    @SerializedName(LATITUDE)
    private double latitutde;

}
