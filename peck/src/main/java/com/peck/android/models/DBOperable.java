package com.peck.android.models;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.peck.android.PeckApp;
import com.peck.android.database.DBType;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mammothbane on 5/28/2014.
 */
public abstract class DBOperable implements Serializable {

    public DBOperable() {
        created = new Date(System.currentTimeMillis());
        updated = new Date(System.currentTimeMillis());
    }

    @DBType("integer primary key autoincrement")
    @SerializedName(PeckApp.Constants.Database.LOCAL_ID)
    protected Integer localId = null;

    @Expose
    @DBType("integer")
    @SerializedName(PeckApp.Constants.Network.SV_ID_NAME)
    protected Integer serverId = null;


    @Expose
    @NonNull
    @SerializedName("created_at")
    @DBType("integer")
    protected Date created;

    @Expose
    @NonNull
    @SerializedName("updated_at")
    @DBType("integer")
    protected Date updated;

    private static final transient String DELIM = ", ";
    public final static transient HashMap<Class, String> tableIds = new HashMap<Class, String>();

    public String getTableName() {
        return "tbl_" + getClass().getSimpleName();
    }

    public String getDatabaseCreate() {
        String dbCreate = "create table " + getTableName() + " (";
        Gson gson = new GsonBuilder().serializeNulls().create();

        JsonObject jsonObject = (JsonObject)new JsonParser().parse(gson.toJson(this, getClass()));

        for (Map.Entry<String, JsonElement> field : jsonObject.entrySet()) {
            dbCreate += field.getKey() + " ";

            JsonElement element = field.getValue();
            if (element.isJsonPrimitive()) {
                if (element.getAsJsonPrimitive().isString()) {
                    dbCreate += "text";
                } else if (element.getAsJsonPrimitive().isNumber()) {
                    if (element.getAsDouble() != ((double) element.getAsInt()))
                        dbCreate += "double";
                    else
                        dbCreate += ("integer" + ((field.getKey().equals(PeckApp.Constants.Database.LOCAL_ID))
                                ? " primary key autoincrement" : ""));
                }
            } else if (element.isJsonNull()) {
                String actualFieldName = "";
                for (Field objField : getClass().getFields()) {  //this block can cause issues if we have fields with the same names as other fields' serializations. don't do that.
                    SerializedName annotation = objField.getAnnotation(SerializedName.class);
                    if (objField.getName().equals(field.getKey())) { actualFieldName = field.getKey(); break; }
                    else if (annotation != null && annotation.value() != null) { actualFieldName = annotation.value(); break; }
                }

                try {
                    DBType dbType = getClass().getField(actualFieldName).getAnnotation(DBType.class);
                    if (dbType == null) dbCreate += "text";
                    else dbCreate += dbType.value();
                } catch (NoSuchFieldException e) {
                    Log.e(getClass().getSimpleName(), "Couldn't get field " + actualFieldName);
                    dbCreate += "text";
                }


            } else {
                dbCreate += "text"; //if we don't know what it is, we're saving it as text
            }
            dbCreate += DELIM;
        }

        dbCreate += "unique (" + PeckApp.Constants.Network.SV_ID_NAME + "));";

        return dbCreate;
    }

    public String[] getColumns() {
        ArrayList<String> columns = new ArrayList<String>();
        for (Map.Entry<String, JsonElement> entry : ((JsonObject)new JsonParser().parse(new GsonBuilder().serializeNulls().create().toJson(this, getClass()))).entrySet()) {
            columns.add(entry.getKey());
        }

        String[] ret = new String[columns.size()];

        return columns.toArray(ret);
    }

    public Integer getServerId() {
        return serverId;
    }

    public DBOperable setServerId(int serverId) {
        this.serverId = serverId;
        updated();
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public DBOperable setCreated(Date created) {
        this.created = created;
        updated();
        return this;
    }

    public Date getUpdated() {
        return updated;
    }

    public DBOperable updated() {
        updated = new Date(System.currentTimeMillis());
        return this;
    }

    public DBOperable setUpdated(Date updated) {
        this.updated = updated;
        return this;
    }

    public Integer getLocalId() {
        return localId;
    }

    public DBOperable setLocalId(int id) {
        localId = id;
        updated();
        return this;
    }

    public static long dateToInt(Date date) {
        if (date == null) return -1;
        else return date.getTime();
    }

    /**
     *
     * check to see if two dboperables are equal
     *
     *
     * @param o object to compare
     * @return true if their local and server ids are the same or null
     */

    @Override
    public boolean equals(Object o) {
        /* horrible, redundant boolean logic, but it's hopefully at least readable */

        if (o == null || !(o instanceof DBOperable) || (getLocalId() == null && getServerId() == null) ||
                (((DBOperable) o).getLocalId() == null && ((DBOperable) o).getServerId() == null)) return false; //if both fields are null on either object, return false.

        if (getLocalId() == null && ((DBOperable) o).getServerId() == null || getServerId() == null && ((DBOperable) o).getLocalId() == null)
            return false; //if alternating fields are null, return false.

        if (getLocalId() == null) {
            //we must have two nonnull serverids
            return serverId.equals(((DBOperable) o).getServerId());
        } else {
            //we must have two nonnull localids
            return localId.equals(((DBOperable) o).getLocalId());
        }

    }

    @Override
    public int hashCode() {
        return 31*getLocalId() + 57*getServerId();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id: " + getLocalId() + " | sv_id: " + getServerId() + "]";
    }

}
