package com.peck.android.interfaces;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

/**
 * Created by mammothbane on 5/28/2014.
 */
public abstract class DBOperable {

    protected int localId = -1;
    protected int serverId = -1;
    protected Date created = new Date(-1);
    protected Date updated = new Date(-1);

    public int getServerId() {
        return serverId;
    }

    public DBOperable setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public DBOperable setCreated(Date created) {
        this.created = created;
        return this;
    }

    public Date getUpdated() {
        return updated;
    }

    public DBOperable setUpdated(Date updated) {
        this.updated = updated;
        return this;
    }

    public int getLocalId() {
        return localId;
    }

    public DBOperable setLocalId(int id) {
        localId = id;
        return this;
    }

    public abstract ContentValues toContentValues();

    public abstract DBOperable fromCursor(Cursor cursor);

    public static long dateToInt(Date date) {
        if (date == null) return 0;
        else return date.getTime();
    }

}
