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
 * Created by mammothbane on 7/28/2014.
 */
@Header(plural = "subscriptions", singular = "subscription")
@UriPath("subscriptions")
public class Subscription extends DBOperable {
    public static final transient String USER_ID = "user_id";
    public static final transient String CATEGORY = "category";
    public static final transient String SUBSCRIBED = "subscribed_to";

    @Expose
    @DBType("integer")
    @SerializedName(USER_ID)
    long userId;

    @Expose
    @SerializedName(CATEGORY)
    String category;

    @Expose
    @DBType("integer")
    @SerializedName(SUBSCRIBED)
    int subscribed;

}
