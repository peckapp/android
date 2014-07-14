package com.peck.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class User extends DBOperable {

    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String USERNAME = "username";
    public static final String FACEBOOK_ID = "facebook_link";
    public static final String BIO = "blurb";

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
