package com.peck.android.models;

import android.view.View;

import com.peck.android.R;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.interfaces.WithLocal;

import java.util.Date;

/**
 * Created by mammothbane on 6/10/2014.
 */
public class Meal implements WithLocal, SelfSetup, HasFeedLayout {
    private int localId;
    private int serverId;

    private Date created;
    private Date updated;
    private String title;

    public int getLocalId() {
        return localId;
    }

    public Meal setLocalId(int id) {
        this.localId = id;
        return this;
    }

    public int getServerId() {
        return serverId;
    }

    public Meal setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public Meal setCreated(Date created) {
        this.created = created;
        return this;
    }

    public Date getUpdated() {
        return updated;
    }

    public Meal setUpdated(Date updated) {
        this.updated = updated;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Meal setTitle(String title) {
        this.title = title;
        return this;
    }

    public int hashCode() {
        return (int)(created.getTime()*13+updated.getTime()*17+getServerId()*307-getLocalId());
    }

    @Override
    public int getResourceId() { //TODO: implement, create layout
        return R.layout.frag_meal;
    }

    @Override
    public void setUp(View v) { //TODO: set up a layout that's passed in with the correct information


    }

}
