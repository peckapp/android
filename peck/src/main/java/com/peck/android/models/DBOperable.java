package com.peck.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.DBType;

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
    public long localId;

    @Expose
    @DBType("integer")
    @SerializedName(SV_ID)
    public long serverId;


    @Expose
    @SerializedName(CREATED_AT)
    @DBType("real")
    public double created;

    @Expose
    @SerializedName(UPDATED_AT)
    @DBType("real")
    public double updated;

    @Expose
    @SerializedName(LOCALE)
    @DBType("integer")
    public long locale;



}
