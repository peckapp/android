package com.peck.android.models;

import android.location.Location;

import com.peck.android.interfaces.WithLocal;

/**
 * Created by mammothbane on 6/11/2014.
 */
public class Locale implements WithLocal {

    private int localId;
    private int serverId;
    private String name;
    private Location location;

    public Location getLocation() {
        return location;
    }

    public Locale setLocation(Location location) {
        this.location = location;
        return this;
    }

    public int getServerId() {
        return serverId;
    }

    public Locale setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Locale setName(String name) {
        this.name = name;
        return this;
    }

    public int getLocalId() {
        return localId;
    }

    public Locale setLocalId(int id) {
        this.localId = id;
        return this;
    }




}
