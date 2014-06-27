package com.peck.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.peck.android.R;
import com.peck.android.adapters.FeedAdapter;
import com.peck.android.database.dataspec.CirclesDataSpec;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.HasFeedLayout;
import com.peck.android.interfaces.SelfSetup;
import com.peck.android.managers.UserManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by mammothbane on 6/12/2014.
 */
public class Circle extends DBOperable implements SelfSetup, HasFeedLayout {

    private ArrayList<Integer> users = new ArrayList<Integer>();

    private String title = "";

    public ArrayList<Integer> getUsers() {
        return users;
    }

    public Circle setUsers(ArrayList<Integer> users) {
        this.users = users;
        return this;
    }

    public HashMap<String, ArrayList<Integer>> getAllJoins() {
        HashMap<String, ArrayList<Integer>> ret = new HashMap<String, ArrayList<Integer>>();
        //ret.put(USERS_SERIAL, users);
        return ret;
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


    public int getServerId() {
        return serverId;
    }

    public Circle setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setUp(View v) {

        ((TextView)v.findViewById(R.id.tv_title)).setText(title);

        //todo: this is temporary, just to get things working for now

        FeedAdapter<User> feedAdapter = new FeedAdapter<User>(R.layout.hlvitem_user);
        feedAdapter.setSource(UserManager.getManager());
        ((AdapterView<ListAdapter>)v.findViewById(R.id.hlv_users)).setAdapter(feedAdapter);


    }

    public Circle setLocalid(int localid) {
        localId = localid;
        return this;
    }

    @Override
    public int getResourceId() {
        return R.layout.lvitem_circle;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(CirclesDataSpec.COLUMN_SERVER_ID, serverId);
        contentValues.put(CirclesDataSpec.COLUMN_CREATED, dateToInt(created));
        contentValues.put(CirclesDataSpec.COLUMN_UPDATED, dateToInt(updated));
        contentValues.put(CirclesDataSpec.COLUMN_TITLE, title);

        return contentValues;
    }

    @Override
    public Circle fromCursor(Cursor cursor) {
        setServerId(cursor.getInt(cursor.getColumnIndex(CirclesDataSpec.COLUMN_SERVER_ID)))
                .setTitle(cursor.getString(cursor.getColumnIndex(CirclesDataSpec.COLUMN_TITLE)))
                .setUpdated(new Date(cursor.getInt(cursor.getColumnIndex(CirclesDataSpec.COLUMN_UPDATED))))
                .setCreated(new Date(cursor.getInt(cursor.getColumnIndex(CirclesDataSpec.COLUMN_CREATED))))
                .setLocalid(cursor.getInt(cursor.getColumnIndex(CirclesDataSpec.COLUMN_LOC_ID)));
        return this;
    }

}
