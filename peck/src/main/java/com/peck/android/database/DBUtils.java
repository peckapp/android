/*
 * Copyright (c) 2014 Peck LLC.
 * All rights reserved.
 */

package com.peck.android.database;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.peck.android.PeckApp;
import com.peck.android.annotations.DBType;
import com.peck.android.annotations.UriPath;
import com.peck.android.models.DBOperable;
import com.peck.android.models.Event;
import com.peck.android.network.JsonUtils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mammothbane on 7/15/2014.
 *
 * general database utils class.
 */
public class DBUtils {
    private static final String DELIM = ", ";
    private static final HashMap<Class, String[]> columnMap = new HashMap<Class, String[]>();
    public static Gson gson = new GsonBuilder().serializeNulls().create();

    /**
     * build a database creation string for the specified class
     *
     * @param tClass the class to build for
     * @return the database creation string
     */
    public static <T extends DBOperable> String getDatabaseCreate(Class<T> tClass) {
        //todo: remove "on conflict replace". we don't want any conflicts going into the db
        return "create table " + getTableName(tClass) + " (" + StringUtils.join(fieldsToCreatorString(tClass), DELIM) +
                DELIM + "unique (" + DBOperable.SV_ID +
                ((tClass.equals(Event.class)) ? ", " + Event.TYPE : "")
                + ") on conflict replace);";
    }

    /**
     * get and return the {@link com.peck.android.annotations.UriPath} annotation for the given class.
     *
     * @param tClass the class to evaluate.
     * @return the {@link com.peck.android.annotations.UriPath} for tClass if it exists. if not, return the class' name.
     */
    @Nullable
    public static <T extends DBOperable> String getTableName(Class<T> tClass) {
        UriPath uriPath = tClass.getAnnotation(UriPath.class);
        return (uriPath != null) ? uriPath.value() : tClass.getSimpleName();
    }

    /**
     * get the gson-serialized name of the given field.
     * @param field the field to evaluate
     * @return the {@link com.google.gson.annotations.SerializedName} of the given field, if it exists, the field's name if not.
     */
    public static String getSerializedFieldName(Field field) {
        SerializedName name = field.getAnnotation(SerializedName.class);
        return name == null ? field.getName() : name.value();
    }

    /**
     * build a creation string for a single class
     *
     * @param tClass the class to build for
     * @return the creation string
     */
    public static ArrayList<String> fieldsToCreatorString(Class tClass) {
        ArrayList<String> ret = new ArrayList<String>();
        for (Field objField : getAllFields(tClass)) {  //this block can cause issues if we have fields with the same names as other fields' serializations. don't do that.
            DBType type = objField.getAnnotation(DBType.class);
            ret.add(getSerializedFieldName(objField) + " " + ((type == null) ? "text" : type.value()));
        }
        return ret;
    }


    /**
     * traverse the class hierarchy and return all fields (including inherited) for a given class
     * @param clss the class to check
     * @return a list of fields
     */
    @NonNull
    public static ArrayList<Field> getAllFields(@NonNull Class clss){
        ArrayList<Field> ret = new ArrayList<Field>();
        for (Field field : clss.getDeclaredFields()) {
            field.setAccessible(true);
            if (!Modifier.isTransient(field.getModifiers())) ret.add(field);
        }
        Class superClss = clss.getSuperclass();
        if (superClss != null) ret.addAll(getAllFields(superClss));
        return ret;
    }

    /**
     * return a string array of columns for the specified class. cached in a static map in this class.
     * @param tClass the class to check
     * @return the array
     */
    public static <T extends DBOperable> String[] getColumns(Class<T> tClass) {
        if (columnMap.get(tClass) == null) {
            ArrayList<String> columns = new ArrayList<String>();
            for (Field field : getAllFields(tClass)) {
                columns.add(getSerializedFieldName(field));
            }
            columnMap.put(tClass, columns.toArray(new String[columns.size()]));
        }

        return columnMap.get(tClass);
    }

    /**
     * build a uri for the specified class.
     * @param tClass the class to build for
     * @return the uri
     */
    public static Uri buildLocalUri(Class tClass) {
        return PeckApp.Constants.Database.BASE_AUTHORITY_URI.buildUpon().appendPath(getTableName(tClass)).build();
    }

    /**
     * receive a contentvalues and asynchronously synchronize it to the database
     *
     * @param uri the uri to query
     * @param unwrappedJsonObject the object to put
     * @param parseClass the class to parse it with
     */
    public static void syncJson(final Uri uri, final JsonObject unwrappedJsonObject, final Class parseClass) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Cursor cursor = PeckApp.getContext().getContentResolver().query(uri, new String[] {DBOperable.SV_ID, DBOperable.UPDATED_AT}, DBOperable.SV_ID + " = ?", new String[] { unwrappedJsonObject.get(DBOperable.SV_ID).getAsString() }, DBOperable.UPDATED_AT);

                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    double date = cursor.getDouble(cursor.getColumnIndex(DBOperable.UPDATED_AT));
                    if (unwrappedJsonObject.get(DBOperable.UPDATED_AT).getAsDouble() >= date) {
                        PeckApp.getContext().getContentResolver().update(uri, JsonUtils.jsonToContentValues(unwrappedJsonObject, parseClass), DBOperable.SV_ID + "= ?", new String[] { Long.toString(unwrappedJsonObject.get(DBOperable.SV_ID).getAsLong())});
                    } else Log.v(DBUtils.class.getSimpleName(), "Json sync cancelled: local version is newer");
                } else {
                    PeckApp.getContext().getContentResolver().insert(uri, JsonUtils.jsonToContentValues(unwrappedJsonObject, parseClass));
                }

                cursor.close();
                return null;
            }
        }.execute();
    }

}
