package com.peck.android.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.Header;
import com.peck.android.annotations.Table;

/**
 * Created by mammothbane on 6/16/2014.
 */
@Header(plural = "push_notifications", singular = "push_notification")
@Table("notifications")
public class Peck extends DBOperable {

    public static final transient String NAME = "title";
    public static final transient String TEXT = "text";

    //todo: assign serializations
    @Expose
    @SerializedName(NAME)
    private String title = "";

    @Expose
    @SerializedName(TEXT)
    private String text = "";

}
