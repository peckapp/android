package com.peck.android.json;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.peck.android.interfaces.DBOperable;

import java.util.Map;


/**
 * Created by mammothbane on 6/20/2014.
 */
public class DatabaseJsonConverter<T extends DBOperable> {
    private static Gson gson = new Gson();
    private static JsonParser parser = new JsonParser();
    private T t;

    public DatabaseJsonConverter(T t) {
        this.t = t;
    }

    @NonNull
    public ContentValues tToContentValues(T t) throws JsonParseException {
        ContentValues ret = new ContentValues();

        JsonObject object = (JsonObject)parser.parse(gson.toJson(t, t.getClass()));

        for (Map.Entry<String, JsonElement> field : object.entrySet()) {
            JsonElement element = field.getValue();
            if (element.isJsonPrimitive()) {
                if (element.getAsJsonPrimitive().isString()) {
                    ret.put(field.getKey(), element.getAsString());
                } else if (element.getAsJsonPrimitive().isNumber()) {
                    ret.put(field.getKey(), element.getAsInt());
                }
            } else if (element.isJsonArray()) {
                ret.put(field.getKey(), element.getAsJsonArray().toString());
            }
        }

        return ret;
    }

    @NonNull
    public T fromCursor(Cursor cursor) {
        T ret;
        JsonObject object = new JsonObject();
        String colName;
        int colType;
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            colType = cursor.getType(i);

        }
        ret = (T)gson.fromJson(object, t.getClass());

        return ret;
    }


}
