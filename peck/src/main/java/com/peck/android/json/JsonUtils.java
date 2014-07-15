package com.peck.android.json;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.peck.android.PeckApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


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

    public static ContentValues jsonToContentValues(JsonObject object) {
        ContentValues ret = new ContentValues();
        for (Map.Entry<String, JsonElement> field : object.entrySet()) {
            JsonElement element = field.getValue();
            if (element.isJsonPrimitive()) {
                if (element.getAsJsonPrimitive().isString()) {
                    ret.put(field.getKey(), element.getAsString());
                } else if (element.getAsJsonPrimitive().isNumber()) {
                    if (element.getAsDouble() != ((double)element.getAsInt())) ret.put(field.getKey(), element.getAsDouble());
                    else ret.put(field.getKey(), element.getAsInt());
                } else if (element.getAsJsonPrimitive().isBoolean()) {
                    ret.put(field.getKey(), element.getAsBoolean());
                }
            } else if (element.isJsonArray()) {
                ret.put(field.getKey(), ARRAY_MARKER + element.getAsJsonArray().toString());
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

    public static JSONObject wrapJson(Class tClass, JsonObject object) throws JSONException {
        JsonObject wrapper = new JsonObject();
        wrapper.add(PeckApp.getJsonHeader(tClass, false), object);
        return new JSONObject(wrapper.toString());
    }

    private static class JsonDateDeserializer implements JsonDeserializer<Date> {
        private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss.SSS'Z'");

        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Date ret = new Date(-1);
            try {
                ret = simpleDateFormat.parse(json.getAsJsonPrimitive().getAsString());
            } catch (ParseException e) {
                Log.e("Json date deserializer", "parse exception: " + e.toString());
            }
            return ret;
        }

    }



}
