package com.peck.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import com.peck.android.R;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mammothbane on 6/12/2014.
 */
public class Circle extends DBOperable implements SelfSetup, HasFeedLayout {

    private ArrayList<User> users = new ArrayList<User>();
    private String title;
    private Date created;
    private Date updated;
    private boolean hidden;
    private int serverId;

    public ArrayList<User> getUsers() {
        return users;
    }

    public Circle setUsers(ArrayList<User> users) {
        this.users = users;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Circle setTitle(String title) {
        this.title = title;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public Circle setCreated(Date created) {
        this.created = created;
        return this;
    }

    public Date getUpdated() {
        return updated;
    }

    public Circle setUpdated(Date updated) {
        this.updated = updated;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    public Circle setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public int getServerId() {
        return serverId;
    }

    public Circle setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }


    //TODO: implement inherited methods

    @Override
    public void setUp(View v) {
        ((TextView)v.findViewById(R.id.tv_title)).setText(title);
        //v.getParent()
        //v.findViewById(R.id.hlv_users);
    }

    @Override
    public int getResourceId() {
        return R.layout.lvitem_circle;
    }

    @Override
    public ContentValues toContentValues() {
        return null;
    }

    @Override
    public DBOperable fromCursor(Cursor cursor) {
        return null;
    }
}
