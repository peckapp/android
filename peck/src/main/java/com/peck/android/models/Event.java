package com.peck.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import com.peck.android.R;
import com.peck.android.database.dataspec.EventDataSpec;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.models.postItems.Post;

import java.util.Date;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class Event extends Post<String> implements HasFeedLayout, SelfSetup {
    private int serverId;
    private int color;
    private Date created;
    private Date updated;
    private String title;
    private String text;


    public String getText() {
        return text;
    }

    public Event setText(String text) {
        this.text = text;
        return this;
    }

    public Event setLocalId(int id) {
        this.localId = id;
        return this;
    }

    public int getServerId() {
        return serverId;
    }

    public Event setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

    public int getColor() {
        return color;
    }

    public Event setColor(int color) {
        this.color = color;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public Event setCreated(Date created) {
        this.created = created;
        return this;
    }

    public Date getUpdated() {
        return updated;
    }

    public Event setUpdated(Date updated) {
        this.updated = updated;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Event setTitle(String title) {
        this.title = title;
        return this;
    }

    public int hashCode() {
        return (int)(created.getTime()*13+updated.getTime()*17+getServerId()*307-getLocalId());
    }

    @Override
    public int getResourceId() { //TODO: implement, create layout
        return R.layout.lvitem_event;
    }



    @Override
    public void setUp(View v) { //TODO: set up a layout that's passed in with the correct information
        ((TextView)v.findViewById(R.id.tv_title)).setText(title);
        ((TextView)v.findViewById(R.id.tv_text)).setText(text);
    }

    public void setUpPost(View v) {

    }

    @Override
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(EventDataSpec.COLUMN_SERVER_ID, getLocalId());
        cv.put(EventDataSpec.COLUMN_COLOR, getColor());
        cv.put(EventDataSpec.COLUMN_TITLE, getTitle());
        cv.put(EventDataSpec.COLUMN_TEXT, getText());

        cv.put(EventDataSpec.COLUMN_UPDATED, dateToInt(getUpdated()));
        cv.put(EventDataSpec.COLUMN_CREATED, dateToInt(getCreated()));

        return cv;
    }

    @Override
    public Event fromCursor(Cursor cursor) {
        return this.setLocalId(cursor.getInt(cursor.getColumnIndex(EventDataSpec.COLUMN_LOC_ID)))
                .setServerId(cursor.getInt(cursor.getColumnIndex(EventDataSpec.COLUMN_SERVER_ID)))
                .setColor(cursor.getInt(cursor.getColumnIndex(EventDataSpec.COLUMN_COLOR)))
                .setTitle(cursor.getString(cursor.getColumnIndex(EventDataSpec.COLUMN_TITLE)))
                .setCreated(new Date(cursor.getLong(cursor.getColumnIndex(EventDataSpec.COLUMN_CREATED))))
                .setUpdated(new Date(cursor.getLong(cursor.getColumnIndex(EventDataSpec.COLUMN_UPDATED))))
                .setText(cursor.getString(cursor.getColumnIndex(EventDataSpec.COLUMN_TEXT)));
    }
}
