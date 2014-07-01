package com.peck.android.models;

import android.support.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.PeckApp;
import com.peck.android.database.DBType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by mammothbane on 5/28/2014.
 */
public abstract class DBOperable implements Serializable {

    public DBOperable() {
        created = new Date(System.currentTimeMillis());
        updated = new Date(System.currentTimeMillis());
    }

    @DBType("integer primary key autoincrement")
    @SerializedName(PeckApp.Constants.Database.LOCAL_ID)
    public Integer localId = null;

    @Expose
    @DBType("integer")
    @SerializedName(PeckApp.Constants.Network.SV_ID_NAME)
    public Integer serverId = null;


    @Expose
    @NonNull
    @SerializedName("created_at")
    @DBType("integer")
    public Date created;

    @Expose
    @NonNull
    @SerializedName("updated_at")
    @DBType("integer")
    public Date updated;

    public String getTableName() {
        return "tbl_" + getClass().getSimpleName();
    }

    public String[] getColumns() {
        ArrayList<String> columns = new ArrayList<String>();
        for (Map.Entry<String, JsonElement> entry : ((JsonObject)new JsonParser().parse(new GsonBuilder().serializeNulls().create().toJson(this, getClass()))).entrySet()) {
            columns.add(entry.getKey());
        }

        String[] ret = new String[columns.size()];

        return columns.toArray(ret);
    }

    public Integer getServerId() {
        return serverId;
    }

    public DBOperable setServerId(int serverId) {
        this.serverId = serverId;
        updated();
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public DBOperable setCreated(Date created) {
        this.created = created;
        updated();
        return this;
    }

    public Date getUpdated() {
        return updated;
    }

    public DBOperable updated() {
        updated = new Date(System.currentTimeMillis());
        return this;
    }

    public DBOperable setUpdated(Date updated) {
        this.updated = updated;
        return this;
    }

    public Integer getLocalId() {
        return localId;
    }

    public DBOperable setLocalId(int id) {
        localId = id;
        updated();
        return this;
    }

    public static long dateToInt(Date date) {
        if (date == null) return -1;
        else return date.getTime();
    }

    /**
     *
     * check to see if two dboperables are equal
     *
     *
     * @param o object to compare
     * @return true if their local and server ids are the same or null
     */

    @Override
    public boolean equals(Object o) {
        /* horrible, redundant boolean logic, but it's hopefully at least readable */

        if (o == null || !(o instanceof DBOperable) || (getLocalId() == null && getServerId() == null) ||
                (((DBOperable) o).getLocalId() == null && ((DBOperable) o).getServerId() == null)) return false; //if both fields are null on either object, return false.

        if (getLocalId() == null && ((DBOperable) o).getServerId() == null || getServerId() == null && ((DBOperable) o).getLocalId() == null)
            return false; //if alternating fields are null, return false.

        if (getLocalId() == null) {
            //we must have two nonnull serverids
            return serverId.equals(((DBOperable) o).getServerId());
        } else {
            //we must have two nonnull localids
            return localId.equals(((DBOperable) o).getLocalId());
        }

    }

    @Override
    public int hashCode() {
        return 31*getLocalId() + 57*getServerId();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id: " + getLocalId() + " | sv_id: " + getServerId() + "]";
    }

}
