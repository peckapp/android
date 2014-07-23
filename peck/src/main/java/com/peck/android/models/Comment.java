package com.peck.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.DBType;
import com.peck.android.annotations.Header;
import com.peck.android.annotations.UriPath;

/**
 * Created by mammothbane on 7/8/2014.
 */
@Header(plural = "comments", singular = "comment")
@UriPath("comments")
public class Comment extends DBOperable {
    public static final transient String COMMENT_FROM = "comment_from";
    public static final transient String USER_ID = "user_id";
    public static final transient String TEXT = "content";
    public static final transient String TYPE = "category";

    @Expose
    @DBType("integer")
    @SerializedName(COMMENT_FROM)
    private long parent;

    @Expose
    @DBType("integer")
    @SerializedName(USER_ID)
    private long userId;

    @Expose
    @SerializedName(TEXT)
    private String text = "";

    @Expose
    @SerializedName(TYPE)
    private String type;

}
