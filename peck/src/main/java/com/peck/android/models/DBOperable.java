package com.peck.android.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.database.DBType;

import java.io.Serializable;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class DBOperable implements Serializable {

    public static final transient String LOCAL_ID = "_id"; //CursorAdapter requires this field to be "_id" exactly.
    public static final transient String SV_ID = "id";
    public static final transient String CREATED_AT = "created_at";
    public static final transient String UPDATED_AT = "updated_at";
    public static final transient String DELETED = "deleted";
    public static final transient String LOCALE = "institution_id";

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
    @SerializedName(CREATED_AT)
    @DBType("integer")
    public int created;

    @Expose
    @SerializedName(UPDATED_AT)
    @DBType("integer")
    public int updated;

    @Expose
    @SerializedName(LOCALE)
    @DBType("integer")
    public int locale;



}
