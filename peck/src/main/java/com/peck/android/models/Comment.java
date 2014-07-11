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

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public CommentType getType() {
        return type;
    }

    public void setType(CommentType type) {
        this.type = type;
    }


}
