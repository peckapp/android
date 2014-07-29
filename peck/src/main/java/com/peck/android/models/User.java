package com.peck.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.Header;
import com.peck.android.annotations.UriPath;

/**
 * Created by mammothbane on 6/18/2014.
 */
@Header(plural = "users", singular = "user")
@UriPath("users")
public class User extends DBOperable {
    public static final transient String FIRST_NAME = "first_name";
    public static final transient String LAST_NAME = "last_name";
    public static final transient String EMAIL = "email";
    public static final transient String FACEBOOK_ID = "facebook_link";
    public static final transient String BIO = "blurb";
    public static final transient String IMAGE_NAME = "image";
    public static final transient String THUMBNAIL = "thumb_image";

    @Expose
    @SerializedName(IMAGE_NAME)
    private String imageName;

    @Expose
    @SerializedName(THUMBNAIL)
    private String thumbnailUrl;

    @Expose
    @SerializedName(FIRST_NAME)
    private String firstName;

    @Expose
    @SerializedName(LAST_NAME)
    private String lastName;

    @Expose
    @SerializedName(EMAIL)
    private String email;

    @Expose
    @SerializedName(FACEBOOK_ID)
    private String fbId;

    @Expose
    @SerializedName(BIO)
    private String bio;

}
