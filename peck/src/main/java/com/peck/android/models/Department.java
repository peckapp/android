package com.peck.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mammothbane on 7/28/2014.
 */
public class Department extends DBOperable {
    public static final transient String NAME = "name";

    @Expose
    @SerializedName(NAME)
    String name;
}
