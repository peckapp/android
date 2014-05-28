package com.peck.android.models;

import android.database.Cursor;

import com.peck.android.database.EventOpenHelper;
import com.peck.android.interfaces.CursorCreatable;

import java.util.Date;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class Event implements CursorCreatable {
    private int localId;
    private int serverId;
    private int color;
    private Date created;
    private Date updated;
    private String title;

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int id) {
        this.localId = id;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Event createFromCursor(Cursor cursor) {
        setLocalId(cursor.getInt(cursor.getColumnIndex(EventOpenHelper.COLUMN_LOC_ID)));
        setServerId(cursor.getInt(cursor.getColumnIndex(EventOpenHelper.COLUMN_SERVER_ID)));
        setColor(cursor.getInt(cursor.getColumnIndex(EventOpenHelper.COLUMN_COLOR)));
        setTitle(cursor.getString(cursor.getColumnIndex(EventOpenHelper.COLUMN_TITLE)));
        return this;
    }

}
