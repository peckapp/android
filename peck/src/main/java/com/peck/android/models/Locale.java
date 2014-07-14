package com.peck.android.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mammothbane on 6/11/2014.
 */
public class Locale extends DBOperable {

    public static final String NAME = "name";
    public static final String ADDRESS = "street_address";
    public static final String STATE = "state";
    public static final String COUNTRY = "country";
    public static final String RANGE = "range";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";


    @NonNull
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
    @Nullable
    @SerializedName(RANGE)
    private Float range = null;

    @Expose
    @Nullable
    @SerializedName(LONGITUDE)
    private Double longitude = null;

    @Expose
    @Nullable
    @SerializedName(LATITUDE)
    private Double latitutde = null;

}
