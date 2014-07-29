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

@Header(plural = "circle_members", singular = "circle_member")
@UriPath("circle_members")
public class CircleMember extends DBOperable {
    public static final transient String CIRCLE_ID = "circle_id";
    public static final transient String USER_ID = "user_id";
    public static final transient String INVITED_BY = "invited_by";
    public static final transient String DATE_ADDED = "date_added";

    @Expose
    @DBType("integer")
    @SerializedName(CIRCLE_ID)
    long circleId;

    @Expose
    @DBType("integer")
    @SerializedName(USER_ID)
    long userId;

    @Expose
    @DBType("integer")
    @SerializedName(INVITED_BY)
    long invitedBy;

    @Expose
    @DBType("real")
    @SerializedName(DATE_ADDED)
    double dateAdded;

}
