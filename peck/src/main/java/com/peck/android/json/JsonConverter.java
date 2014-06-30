package com.peck.android.json;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.peck.android.interfaces.DBOperable;

import java.lang.reflect.Type;
import java.util.Map;


/**
 * Created by mammothbane on 6/20/2014.
 */
public class JsonConverter<T extends DBOperable> {
    private static Gson gson = new Gson();
    private static JsonParser parser = new JsonParser();

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
                ret.put(field.getKey(), element.getAsJsonArray().toString());
            }
        }

        return ret;
    }

    @NonNull
    public T fromCursor(Cursor cursor) {
        T ret;
        JsonObject object = new JsonObject();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            int colType = cursor.getType(i);
            String colName = cursor.getColumnName(i);
            switch (colType) {
                case Cursor.FIELD_TYPE_STRING:
                    object.addProperty(colName, cursor.getString(i));
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
        Type type = new TypeToken<T>(){}.getType();
        ret = gson.fromJson(object, type);

        return ret;
    }


}
