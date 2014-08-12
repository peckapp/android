/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.Header;
import com.peck.android.annotations.UriPath;

/**
 * Created by mammothbane on 6/16/2014.
 */
@Header(plural = "pecks", singular = "peck")
@UriPath("pecks")
public class Peck extends DBOperable {

    public static final transient String NOTIFICATION_TYPE = "NOTIFICATION_TYPE";
    public static final transient String TEXT = "message";
    public static final transient String INVITED_TO = "invitation";
    public static final transient String INVITED_BY = "invited_by";

    @Expose
    @SerializedName(INVITED_TO)
    private long invitedTo;

    @Expose
    @SerializedName(INVITED_BY)
    private long invitedBy;

    //todo: assign serializations
    @Expose
    @SerializedName(NOTIFICATION_TYPE)
    private String type;

    @Expose
    @SerializedName(TEXT)
    private String text;

}
