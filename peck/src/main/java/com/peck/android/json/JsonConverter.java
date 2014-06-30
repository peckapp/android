package com.peck.android.json;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.peck.android.PeckApp;
import com.peck.android.models.DBOperable;

import java.util.Map;


/**
 * Created by mammothbane on 6/20/2014.
 *
 * this class prepares model objects for insertion into the database
 * and restores them from database query cursors.
 *
 */


public class JsonConverter<T extends DBOperable> {
    private static Gson gson = new Gson();
    private static JsonParser parser = new JsonParser();
    private static final String ARRAY_MARKER = "#jsonarray#";

    @NonNull
    public ContentValues toContentValues(T t) throws JsonParseException {
        ContentValues ret = new ContentValues();

        JsonObject object = (JsonObject)parser.parse(gson.toJson(t, t.getClass()));

        for (Map.Entry<String, JsonElement> field : object.entrySet()) {
            JsonElement element = field.getValue();
            if (element.isJsonPrimitive()) {
                if (element.getAsJsonPrimitive().isString()) {
                    ret.put(field.getKey(), element.getAsString());
                } else if (element.getAsJsonPrimitive().isNumber()) {
                    if (element.getAsDouble() != ((double)element.getAsInt())) ret.put(field.getKey(), element.getAsDouble());
                    else ret.put(field.getKey(), element.getAsInt());
                }
            } else if (element.isJsonArray()) {
                ret.put(field.getKey(), ARRAY_MARKER + element.getAsJsonArray().toString());
            }
        }

        if (ret.containsKey(PeckApp.Constants.Database.LOCAL_ID)) ret.remove(PeckApp.Constants.Database.LOCAL_ID);

        return ret;
    }

    @NonNull
    public T fromCursor(Cursor cursor, Class<T> tClass) {
        T ret;
        JsonObject object = new JsonObject();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            int colType = cursor.getType(i);
            String colName = cursor.getColumnName(i);
            switch (colType) {
                case Cursor.FIELD_TYPE_STRING:
                    String s = cursor.getString(i);
                    if (s.contains(ARRAY_MARKER)) object.add(colName, parser.parse(s));
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

        ret = gson.fromJson(object, tClass);

        return ret;
    }

}
