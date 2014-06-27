package com.peck.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.R;
import com.peck.android.database.dataspec.EventDataSpec;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;

import java.util.Date;

/**
 * Created by mammothbane on 5/28/2014.
 */
public class Event extends DBOperable implements HasFeedLayout, SelfSetup {

    @NonNull
    @Expose
    @SerializedName("start_date")
    private Date startTime = new Date(-1);

    @NonNull
    @Expose
    @SerializedName("end_date")
    private Date endTime = new Date(-1);

    @Expose
    private String title = "";

    @Expose
    @SerializedName("event_description")
    private String text = "";

    @Expose
    @SerializedName("id")
    private int serverId = -1;

    @Expose
    @SerializedName("")
    private String profilePictureURL;


    public Date getStartTime() {
        return startTime;
    }

    public Event setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Event setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

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


    @Override
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(EventDataSpec.COLUMN_SERVER_ID, getLocalId());
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
                .setTitle(cursor.getString(cursor.getColumnIndex(EventDataSpec.COLUMN_TITLE)))
                .setCreated(new Date(cursor.getLong(cursor.getColumnIndex(EventDataSpec.COLUMN_CREATED))))
                .setUpdated(new Date(cursor.getLong(cursor.getColumnIndex(EventDataSpec.COLUMN_UPDATED))))
                .setText(cursor.getString(cursor.getColumnIndex(EventDataSpec.COLUMN_TEXT)));
    }
}
