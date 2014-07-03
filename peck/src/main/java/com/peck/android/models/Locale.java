package com.peck.android.models;

import android.app.Activity;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.R;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.managers.LocaleManager;

/**
 * Created by mammothbane on 6/11/2014.
 */
public class Locale extends DBOperable implements SelfSetup, HasFeedLayout, Comparable<Locale> {

    private final static int resId = R.layout.lvitem_locale;


    @NonNull
    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("street_address")
    private String streetAddress;

    @Expose
    @SerializedName("state")
    private String state;

    @Expose
    @SerializedName("country")
    private String country;

    @Expose
    @Nullable
    @SerializedName("range")
    private Float range = null;

    @Expose
    @Nullable
    @SerializedName("gps_longitude")
    private Double longitude = null;

    @Expose
    @Nullable
    @SerializedName("gps_latitude")
    private Double latitutde = null;

    private Location location;
    private transient double dist; //don't add to database

    public Location getLocation() {
        if (location == null) {
            location = new Location("null");
            if (range != null) location.setAccuracy(range);
            if (longitude != null) location.setLongitude(longitude);
            if (latitutde != null) location.setLatitude(latitutde);
        }
        return location;
    }


    @Nullable
    public Double getDist() {
        if (location == null || LocaleManager.getLocation() == null) return null;

        return (Math.sqrt(Math.pow((LocaleManager.getLocation().getLongitude() - getLocation().getLongitude()), 2) + Math.pow((LocaleManager.getLocation().getLatitude() - getLocation().getLatitude()), 2)));
    }

    public Locale setLocation(Location location) {
        this.location = location;
        return this;
    }

    public String getName() {
        return name;
    }

    public Locale setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public void setUp(@NonNull View v, Activity activity) {
        ((TextView)v.findViewById(R.id.tv_locale_name)).setText(toString());
    }

    @Override
    public int getResourceId() {
        return resId;
    }

    @Override
    public int compareTo(@NonNull Locale locale) {
        Double myDist = getDist();
        Double theirDist = locale.getDist();

        if (myDist == null) {
            if (theirDist == null) return 0;
            else return -1;
        } else if (theirDist == null) return 1;

        return ((int)Math.signum(this.getDist() - locale.getDist()));
    }

    @Override
    public String toString() {
        return getName();
    }
}
