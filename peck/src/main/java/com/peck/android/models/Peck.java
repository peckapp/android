package com.peck.android.models;


import com.google.gson.annotations.Expose;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class Peck extends DBOperable {

    //todo: assign serializations
    @Expose
    private String title = "";

    @Expose
    private String text = "";

}
