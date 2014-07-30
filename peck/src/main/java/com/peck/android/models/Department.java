package com.peck.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.Header;
import com.peck.android.annotations.UriPath;

/**
 * Created by mammothbane on 7/28/2014.
 */

@UriPath("departments")
@Header(singular = "department", plural = "departments")
public class Department extends DBOperable {
    public static final transient String NAME = "name";

    @Expose
    @SerializedName(NAME)
    String name;
}
