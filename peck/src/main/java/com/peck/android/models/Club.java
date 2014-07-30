package com.peck.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.DBType;
import com.peck.android.annotations.Header;
import com.peck.android.annotations.UriPath;

/**
 * Created by mammothbane on 7/28/2014.
 */

@UriPath("clubs")
@Header(plural = "clubs", singular = "club")
public class Club extends DBOperable {
    public static final transient String NAME = "club_name";
    public static final transient String DESCRIPTION = "description";
    public static final transient String CREATOR_OWNER = "user_id";

    @Expose
    @SerializedName(CREATOR_OWNER)
    @DBType("integer")
    long creatorOwner;

    @Expose
    @SerializedName(NAME)
    String name;

    @Expose
    @SerializedName(DESCRIPTION)
    String description;

}
