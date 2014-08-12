/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android.models.joins;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.DBType;
import com.peck.android.annotations.Header;
import com.peck.android.annotations.UriPath;
import com.peck.android.models.DBOperable;

/**
 * Created by mammothbane on 7/28/2014.
 */

@Header(singular = "event_attendee", plural = "event_attendees")
@UriPath("event_attendees")
public class EventAttendee extends DBOperable {
    public static final transient String USER_ID = "user_id";
    public static final transient String ADDED_BY = "added_by";
    public static final transient String CATEGORY = "category";
    public static final transient String ATTENDED = "event_attended";

    @Expose
    @DBType("integer")
    @SerializedName(USER_ID)
    long userId;

    @Expose
    @DBType("integer")
    @SerializedName(ADDED_BY)
    long addedBy;

    @Expose
    @SerializedName(CATEGORY)
    String category;

    @Expose
    @DBType("integer")
    @SerializedName(ATTENDED)
    long attended;
}
