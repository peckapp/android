package com.peck.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.enums.CommentType;

/**
 * Created by mammothbane on 7/8/2014.
 */
public class Comment extends DBOperable {

    public static final transient String COMMENT_FROM = "comment_from";
    public static final transient String USER_ID = "user_id";
    public static final transient String TEXT = "content";
    public static final transient String TYPE = "category";

    @Expose
    @SerializedName(COMMENT_FROM)
    private Integer parent;

    @Expose
    @SerializedName(USER_ID)
    private Integer userId;

    @Expose
    @SerializedName(TEXT)
    private String text = "";

    @Expose
    @SerializedName(TYPE)
    private CommentType type;

}
