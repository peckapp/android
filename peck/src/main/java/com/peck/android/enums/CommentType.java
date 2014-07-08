package com.peck.android.enums;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mammothbane on 7/8/2014.
 */
public enum CommentType {

    @Expose
    @SerializedName("simple_event")
    SIMPLE_EVENT,

    @Expose
    @SerializedName("circle")
    CIRCLE

}
