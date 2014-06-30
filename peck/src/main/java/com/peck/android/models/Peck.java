package com.peck.android.models;

import android.view.View;
import android.widget.TextView;

import com.peck.android.R;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

import java.util.Date;

/**
 * Created by mammothbane on 6/16/2014.
 */
public class Peck extends DBOperable implements SelfSetup, HasFeedLayout {
    private int serverId = -1;
    private Date created = new Date(-1);
    private Date updated = new Date(-1);
    private String title = "";
    private String text = "";
    private int color = -1;
    private boolean seen;

    public boolean isSeen() {
        return seen;
    }

    public Peck setSeen(boolean seen) {
        this.seen = seen;
        return this;
    }

    public Peck setLocalId(int localId) {
        this.localId = localId;
        return this;
    }

    public int getServerId() {
        return serverId;
    }

    public Peck setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public Peck setCreated(Date created) {
        this.created = created;
        return this;
    }

    public Date getUpdated() {
        return updated;
    }

    public Peck setUpdated(Date updated) {
        this.updated = updated;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Peck setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getText() {
        return text;
    }

    public Peck setText(String text) {
        this.text = text;
        return this;
    }

    public int getColor() {
        return color;
    }

    public Peck setColor(int color) {
        this.color = color;
        return this;
    }

    @Override
    public int getResourceId()
    {
        return R.layout.lvitem_peck;
    }

    @Override
    public void setUp(View v) {
        ((TextView)v.findViewById(R.id.tv_text)).setText(text);
        ((TextView)v.findViewById(R.id.tv_title)).setText(title);
    }
}
