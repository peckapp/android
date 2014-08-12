/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.DBType;

import java.io.Serializable;

/**
 * Created by mammothbane on 5/28/2014.
 *
 * base model. strategy is as follows: define public static final transient {@link java.lang.String}s matching serverside field names.
 * transience is to ensure that fields don't get serialized.
 * add fields with any level of security (though my convention has been private) and annotate them with {@link com.google.gson.annotations.SerializedName}.
 * if received from the server, field needs to be annotated {@link com.google.gson.annotations.Expose}.
 * if not a String value, field needs to be annotated {@link com.peck.android.annotations.DBType} with the appropriate SQLite3 type.
 * all models should conform to this pattern and extend this class.
 *
 * @author mammothbane
 * @since 1.0
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
    private boolean pendingDeletion = false;

    @DBType("integer primary key autoincrement")
    @SerializedName(LOCAL_ID)
    private long localId;

    @Expose
    @DBType("integer")
    @SerializedName(SV_ID)
    private long serverId;


    @Expose
    @SerializedName(CREATED_AT)
    @DBType("real")
    private double created;

    @Expose
    @SerializedName(UPDATED_AT)
    @DBType("real")
    private double updated;

    @Expose
    @SerializedName(LOCALE)
    @DBType("integer")
    private long locale;

}
