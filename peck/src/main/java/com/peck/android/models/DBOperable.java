package com.peck.android.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.database.DBType;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mammothbane on 5/28/2014.
 */
public abstract class DBOperable implements Serializable {

    public static final transient String LOCAL_ID = "_id"; //CursorAdapter requires this field to be "_id" exactly.
    public static final transient String SV_ID = "id";
    public static final transient String CREATED_AT = "created_at";
    public static final transient String UPDATED_AT = "updated_at";
    public static final transient String DELETED = "deleted";

    @SerializedName(DELETED)
    @DBType("boolean")
    public boolean pendingDeletion = false;

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
    @SerializedName(CREATED_AT)
    @DBType("integer")
    public Date created;

    @Expose
    @NonNull
    @SerializedName(UPDATED_AT)
    @DBType("integer")
    public Date updated;


    public DBOperable() {
        created = new Date(System.currentTimeMillis());
        updated = new Date(System.currentTimeMillis());
    }

}
