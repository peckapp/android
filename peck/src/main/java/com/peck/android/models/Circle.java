package com.peck.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.Header;
import com.peck.android.annotations.UriPath;

import java.util.ArrayList;


/**
 * Created by mammothbane on 6/12/2014.
 */

@Header(plural = "circles", singular = "circle")
@UriPath("circles")
public class Circle extends DBOperable {
    public static final transient String MEMBERS = "circle_members";
    public static final transient String NAME = "circle_name";
    public static final transient String IMAGE_URL = "image_link";
    public static final transient String USER_ID = "user_id";

    @Expose
    @SerializedName(USER_ID)
    private long userId;

    @Expose
    @SerializedName(MEMBERS)
    private ArrayList<Integer> userIds = new ArrayList<Integer>();

    @Expose
    @SerializedName(NAME)
    private String title;

    @Expose
    @SerializedName(IMAGE_URL)
    private String imageUrl;

}
