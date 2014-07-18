package com.peck.android.json;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.peck.android.PeckApp;
import com.peck.android.database.DBUtils;

import org.json.JSONException;

import java.lang.reflect.Field;


/**
 * Created by mammothbane on 6/20/2014.
 *
 * prepares model objects for insertion into the database
 * and restores them from database query cursors.
 *
 */


public class JsonUtils {


    public static final String ARRAY_MARKER = "#jsonarray#";
    public static JsonParser parser = new JsonParser();

    private JsonUtils() {}

    public static ContentValues jsonToContentValues(JsonObject object, Class tClass) {
        ContentValues ret = new ContentValues();

        for (Field field : DBUtils.getAllFields(tClass)) {
            String s = DBUtils.getSerializedFieldName(field);
            JsonElement element = object.get(s);
            if (element != null) {
                if (element.isJsonPrimitive()) {
                    if (element.getAsJsonPrimitive().isString()) {
                        ret.put(s, element.getAsString());
                    } else if (element.getAsJsonPrimitive().isNumber()) {
                        if (field.getType().equals(double.class) || field.getType().equals(float.class)
                                || field.getType().equals(Double.class) || field.getType().equals(Float.class)) {
                            ret.put(s, element.getAsDouble());
                        } else if (field.getType().equals(long.class) || field.getType().equals(Long.class)) ret.put(s, element.getAsLong());
                        else ret.put(s, element.getAsInt());
                    } else if (element.getAsJsonPrimitive().isBoolean()) {
                        ret.put(s, element.getAsBoolean());
                    }
                } else if (element.isJsonArray()) {
                    ret.put(s, ARRAY_MARKER + element.getAsJsonArray().toString());
                }
            }
        }

        return ret;
    }

    public static JsonObject cursorToJson(Cursor cursor) {
        JsonObject object = new JsonObject();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            int colType = cursor.getType(i);
            String colName = cursor.getColumnName(i);
            switch (colType) {
                case Cursor.FIELD_TYPE_STRING:
                    String s = cursor.getString(i);
                    if (s.contains(ARRAY_MARKER)) object.add(colName, parser.parse(s.replace(ARRAY_MARKER, "")));
                    else object.addProperty(colName, s);
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    object.addProperty(colName, cursor.getInt(i));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    object.addProperty(colName, cursor.getDouble(i));
                    break;
                default:
                    //todo: throw an exception that the column couldn't be added
                    break;
            }

        }
        return object;
    }

    public static JsonObject wrapJson(Class tClass, JsonObject object) throws JSONException {
        JsonObject wrapper = new JsonObject();
        wrapper.add(PeckApp.getJsonHeader(tClass, false), object);
        return wrapper;
    }
}
