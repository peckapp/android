package com.peck.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import com.peck.android.R;
import com.peck.android.database.helper.EventOpenHelper;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

import java.util.Date;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class Event extends DBOperable implements SelfSetup, HasFeedLayout {
    private int localId;
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

    public int getLocalId() {
        return localId;
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
        return R.layout.frag_event;
    }

    @Override
    public void setUp(View v) { //TODO: set up a layout that's passed in with the correct information
        ((TextView)v.findViewById(R.id.tv_title)).setText(title);
        ((TextView)v.findViewById(R.id.tv_text)).setText(text);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(EventOpenHelper.COLUMN_SERVER_ID, getLocalId());
        cv.put(EventOpenHelper.COLUMN_COLOR, getColor());
        cv.put(EventOpenHelper.COLUMN_TITLE, getTitle());
        cv.put(EventOpenHelper.COLUMN_TEXT, getText());
        cv.put(EventOpenHelper.COLUMN_LOC_ID, getLocalId());

        cv.put(EventOpenHelper.COLUMN_UPDATED, dateToInt(getUpdated()));
        cv.put(EventOpenHelper.COLUMN_CREATED, dateToInt(getCreated()));

        return cv;
    }

    @Override
    public Event fromCursor(Cursor cursor) {
        cursor.moveToFirst();

        return this.setLocalId(cursor.getInt(cursor.getColumnIndex(EventOpenHelper.COLUMN_LOC_ID)))
                .setServerId(cursor.getInt(cursor.getColumnIndex(EventOpenHelper.COLUMN_SERVER_ID)))
                .setColor(cursor.getInt(cursor.getColumnIndex(EventOpenHelper.COLUMN_COLOR)))
                .setTitle(cursor.getString(cursor.getColumnIndex(EventOpenHelper.COLUMN_TITLE)))
                .setCreated(new Date(cursor.getLong(cursor.getColumnIndex(EventOpenHelper.COLUMN_CREATED))))
                .setUpdated(new Date(cursor.getLong(cursor.getColumnIndex(EventOpenHelper.COLUMN_UPDATED))))
                .setText(cursor.getString(cursor.getColumnIndex(EventOpenHelper.COLUMN_TEXT)));
    }
}
