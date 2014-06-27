package com.peck.android.json;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.peck.android.interfaces.DBOperable;

import java.util.ArrayList;
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
            if (field.getValue().isJsonPrimitive()) {
                JsonPrimitive primitive = field.getValue().getAsJsonPrimitive();
                if (primitive.isString()) {
                    ret.put(field.getKey(), primitive.getAsString());
                } else if (primitive.isNumber()) {
                    ret.put(field.getKey(), primitive.getAsInt());
                }
            } //we ignore lists here; they should be handled in getAllJoins
        }

        for (Map.Entry<String, ArrayList<Integer>> entry : t.getAllJoins().entrySet()) {
            String s = parser.parse(gson.toJson(entry.getValue())).toString();
            ret.put(entry.getKey(), s);
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
