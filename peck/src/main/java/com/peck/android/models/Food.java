package com.peck.android.models;

import android.support.annotation.NonNull;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class Food extends DBOperable {
    @NonNull
    private String title = "";
    private String text = "";
    private int type = -1; //vegetable, meat, starch, etc.


    public int getType() {
        return type;
    }

    public Food setType(int type) {
        this.type = type;
        return this;
    }

    public String getText() {
        return text;
    }

    public Food setText(String text) {
        this.text = text;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Food setTitle(String title) {
        this.title = title;
        return this;
    }


}
