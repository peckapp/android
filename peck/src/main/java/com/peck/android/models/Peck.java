/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.DBType;
import com.peck.android.annotations.Header;
import com.peck.android.annotations.UriPath;

/**
 * Created by mammothbane on 6/16/2014.
 */
@Header(plural = "pecks", singular = "peck")
@UriPath("pecks")
public class Peck extends DBOperable {

    public static final transient String NOTIFICATION_TYPE = "notification_type";
    public static final transient String TEXT = "message";
    public static final transient String INVITATION = "invitation";
    public static final transient String INVITED_BY = "invited_by";
    public static final transient String REFERS_TO = "refers_to";
    public static final transient String USER_ID = "user_id";

    @Expose
    @DBType("integer")
    @SerializedName(USER_ID)
    private long userId;


    @Expose
    @DBType("integer")
    @SerializedName(REFERS_TO)
    private long refersTo;

    @Expose
    @SerializedName(INVITATION)
    private String invitation;

    @Expose
    @DBType("integer")
    @SerializedName(INVITED_BY)
    private long invitedBy;

    @Expose
    @SerializedName(NOTIFICATION_TYPE)
    private String type;

    @Expose
    @SerializedName(TEXT)
    private String text;

}
