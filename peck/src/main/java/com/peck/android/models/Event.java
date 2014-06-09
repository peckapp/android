package com.peck.android.models;

import com.peck.android.interfaces.withLocal;

import java.util.Date;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class Event implements withLocal {
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

    public int hashCode() {
        return (int)(created.getTime()*13+updated.getTime()*17+getServerId()*307-getLocalId());
    }

}
