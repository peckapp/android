package com.peck.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.enums.CommentType;

/**
 * Created by mammothbane on 7/8/2014.
 */
public class Comment extends DBOperable {

    @Expose
    @SerializedName("comment_from")
    private Integer parent;

    @Expose
    @SerializedName("user_id")
    private Integer userId;

    @Expose
    @SerializedName("content")
    private String text = "";

    @Expose
    @SerializedName("category")
    private CommentType type;

}
