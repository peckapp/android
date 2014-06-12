package com.peck.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.view.View;

import com.peck.android.PeckApp;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class Food extends DBOperable implements SelfSetup, HasFeedLayout {
    private int localId;
    private int serverId;
    private int parentId;
    private String title;
    private String text;
    private int color;
    private int type; //vegetable, meat, starch, etc.


    public int getType() {
        return type;
    }

    public Food setType(int type) {
        this.type = type;
        return this;
    }

    public int getLocalId() {

        return localId;
    }

    public Food setLocalId(int localId) {
        this.localId = localId;
        return this;
    }

    public int getServerId() {
        return serverId;
    }

    public Food setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

    public String getText() {
        return text;
    }

    public Food setText(String text) {
        this.text = text;
        return this;
    }

    public int getColor() {
        return color;
    }

    public Food setColor(int color) {
        this.color = color;
        return this;
    }

    public int getMealId() {
        return parentId;
    }

    public Food setMealId(int meal) {
        this.parentId = meal;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Food setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public int getResourceId() {
        return 0;
    }

    @Override
    public void setUp(View v) {

    }

    @Override
    public ContentValues toContentValues() {
        return null;
    }

    @Override
    public Food fromCursor(Cursor cursor) {
        return null;
    }

}
