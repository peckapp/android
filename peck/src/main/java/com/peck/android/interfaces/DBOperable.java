package com.peck.android.interfaces;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.database.dataspec.DataSpec;
import com.peck.android.database.dataspec.EventDataSpec;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mammothbane on 5/28/2014.
 */
public abstract class DBOperable implements Serializable {

    private static final transient String DELIM = ", ";
    public final static transient HashMap<Class, String> tableIds = new HashMap<Class, String>();

    public String getTableName() {
        return "tbl_" + getClass().getSimpleName();
    }

    public String getDatabaseCreate() {
        String dbCreate = "create table " + getTableName() + " (";
        Gson gson = new Gson();

        JsonObject jsonObject = (JsonObject)new JsonParser().parse(gson.toJson(this, getClass()));

        for (Map.Entry<String, JsonElement> field : jsonObject.entrySet()) {
            dbCreate += field.getKey() + " ";

            JsonElement element = field.getValue();
            if (element.isJsonPrimitive()) {
                if (element.getAsJsonPrimitive().isString()) {
                    dbCreate += "text";
                } else if (element.getAsJsonPrimitive().isNumber()) {
                    if (element.getAsDouble() != ((double)element.getAsInt())) dbCreate += "double";
                    else dbCreate += "integer";
                }
            } else {
                dbCreate += "text"; //if we don't know what it is, we're saving it as text
            }
            dbCreate += DELIM;
        }
        return dbCreate.substring(0, (dbCreate.length() - DELIM.length())) + ");";
    }


    protected int localId = -1;

    @Expose
    @SerializedName("id")
    protected int serverId = -1;


    @Expose
    @SerializedName("created_at")
    protected Date created = new Date(-1);

    @Expose
    @SerializedName("updated_at")
    protected Date updated = new Date(-1);

    public abstract int getServerId();

    public abstract DBOperable setServerId(int serverId);

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

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DataSpec.COLUMN_SERVER_ID, serverId);
        contentValues.put(DataSpec.COLUMN_CREATED, dateToInt(created));
        contentValues.put(DataSpec.COLUMN_UPDATED, dateToInt(updated));

        return contentValues;
    }

    public DBOperable fromCursor(Cursor cursor) {
        return this.setLocalId(cursor.getInt(cursor.getColumnIndex(EventDataSpec.COLUMN_LOC_ID)))
                .setServerId(cursor.getInt(cursor.getColumnIndex(EventDataSpec.COLUMN_SERVER_ID)))
                .setCreated(new Date(cursor.getLong(cursor.getColumnIndex(EventDataSpec.COLUMN_CREATED))))
                .setUpdated(new Date(cursor.getLong(cursor.getColumnIndex(EventDataSpec.COLUMN_UPDATED))));

    }

    public static long dateToInt(Date date) {
        if (date == null) return -1;
        else return date.getTime();
    }


}
