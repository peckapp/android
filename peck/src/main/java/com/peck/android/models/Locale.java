package com.peck.android.models;

import android.location.Location;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.PeckApp;
import com.peck.android.R;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

/**
 * Created by mammothbane on 6/11/2014.
 */
public class Locale extends DBOperable implements SelfSetup, HasFeedLayout {

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
    @SerializedName("range")
    private double range = (double) PeckApp.Constants.NULL;

    @Expose
    @SerializedName("gps_longitude")
    private double longitude = (double) PeckApp.Constants.NULL;

    @Expose
    @SerializedName("gps_latitude")
    private double latitutde = (double) PeckApp.Constants.NULL;

    private Location location;
    private transient double dist; //don't add to database

    public Location getLocation() {
        if (location == null) {
            location = new Location("null");
            if (range > 0) location.setAccuracy((float)range);
            if (longitude > -300f) location.setLongitude(longitude);
            if (latitutde > -300f) location.setLatitude(latitutde);
        }
        return location;
    }

    public double getDist() {
        return dist;
    }

    public Locale calcDist(Location l) {
        dist = Math.sqrt(Math.pow((l.getLongitude() - getLocation().getLongitude()), 2) + Math.pow((l.getLatitude() - getLocation().getLatitude()), 2));
        return this;
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
    public void setUp(View v) {
        ((TextView)v.findViewById(R.id.tv_locale_name)).setText(toString());
    }

    @Override
    public int getResourceId() {
        return resId;
    }

}
