package com.peck.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.annotations.Header;
import com.peck.android.annotations.Table;

/**
 * Created by mammothbane on 6/18/2014.
 */
@Header(plural = "users", singular = "user")
@Table("users")
public class User extends DBOperable {
    public static final transient String FIRST_NAME = "first_name";
    public static final transient String LAST_NAME = "last_name";
    public static final transient String USERNAME = "username";
    public static final transient String FACEBOOK_ID = "facebook_link";
    public static final transient String BIO = "blurb";

    @Expose
    @SerializedName(FIRST_NAME)
    private String firstName;

    @Expose
    @SerializedName(LAST_NAME)
    private String lastName;

    @Expose
    @SerializedName(USERNAME)
    private String username;

    @Expose
    @SerializedName(FACEBOOK_ID)
    private String fbId = "";

    @Expose
    @SerializedName(BIO)
    private String bio = "";

}
