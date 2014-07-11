package com.peck.android.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.interfaces.HasImage;

/**
 * Created by mammothbane on 6/18/2014.
 */
public class User extends DBOperable implements HasImage {

    @Expose
    @SerializedName("first_name")
    private String firstName;

    @Expose
    @SerializedName("last_name")
    private String lastName;

    @Expose
    @SerializedName("username")
    private String username;

    @Expose
    @SerializedName("facebook_link")
    private String fbId = "";

    @Expose
    @SerializedName("blurb")
    private String bio = "";

    @Expose
    private String profileUrl = "";


    @Nullable
    public String getFbId() {
        return fbId;
    }

    public User setFbId(String fbId) {
        this.fbId = fbId;
        return this;
    }

    @Nullable
    public String getBio() {
        return bio;
    }

    public User setBio(String bio) {
        this.bio = bio;
        return this;
    }

    @Nullable
    public String getImageUrl() {
        return profileUrl;
    }

    public User setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
        return this;
    }

    public String getFullName() {
        return (firstName == null || lastName == null) ? firstName : firstName + " " + lastName;
    }

    public void setFullName(String string) {
        String[] temp = string.split(" ");
        //todo: support for names with multiple spaces?
        firstName = temp[0];
        lastName = temp[1];
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NonNull String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(@NonNull String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

}
