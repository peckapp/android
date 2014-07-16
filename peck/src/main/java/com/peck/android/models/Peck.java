package com.peck.android.models;


import com.google.gson.annotations.Expose;
import com.peck.android.annotations.Header;

/**
 * Created by mammothbane on 6/16/2014.
 */
@Header(plural = "push_notifications", singular = "push_notification")
public class Peck extends DBOperable {

    //todo: assign serializations
    @Expose
    private String title = "";

    @Expose
    private String text = "";

}
