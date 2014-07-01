package com.peck.android.models;

import android.support.annotation.NonNull;
import android.view.View;

import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class Food extends DBOperable implements SelfSetup, HasFeedLayout {
    @NonNull
    private String title = "";
    private String text = "";
    private int type = -1; //vegetable, meat, starch, etc.


    public int getType() {
        return type;
    }

    public Food setType(int type) {
        this.type = type;
        updated();
        return this;
    }

    public String getText() {
        return text;
    }

    public Food setText(String text) {
        this.text = text;
        updated();
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Food setTitle(String title) {
        this.title = title;
        updated();
        return this;
    }

    @Override
    public int getResourceId() {
        return 0;
    }

    @Override
    public void setUp(View v) {

    }

}
