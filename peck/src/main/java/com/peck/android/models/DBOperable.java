package com.peck.android.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.database.DBType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by mammothbane on 5/28/2014.
 */
public abstract class DBOperable implements Serializable {

    public static final String LOCAL_ID = "loc_id";
    public static final String SV_ID = "id";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";


    @DBType("integer primary key autoincrement")
    @SerializedName(LOCAL_ID)
    public Integer localId = null;

    @Expose
    @Nullable
    @DBType("integer")
    @SerializedName(SV_ID)
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


    public DBOperable() {
        created = new Date(System.currentTimeMillis());
        updated = new Date(System.currentTimeMillis());
    }

    public String getTableName() {
        return "tbl_" + getClass().getSimpleName().toLowerCase();
    }

    public String[] getColumns() {
        ArrayList<String> columns = new ArrayList<String>();
        for (Map.Entry<String, JsonElement> entry : ((JsonObject)new JsonParser().parse(new GsonBuilder().serializeNulls().create().toJson(this, getClass()))).entrySet()) {
            columns.add(entry.getKey());
        }

        String[] ret = new String[columns.size()];

        return columns.toArray(ret);
    }

}
