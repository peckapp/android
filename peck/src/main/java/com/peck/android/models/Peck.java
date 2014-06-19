package com.peck.android.models;

import android.content.ContentValues;
import android.database.Cursor;
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
    private int serverId;
    private Date created;
    private Date updated;
    private String title;
    private String text;
    private int color;
    private boolean seen;

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public ContentValues toContentValues() {
        return null;
    }

    @Override
    public DBOperable fromCursor(Cursor cursor) {
        return null;
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
