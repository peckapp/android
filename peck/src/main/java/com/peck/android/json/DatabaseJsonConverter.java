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
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.peck.android.interfaces.DBOperable;

import java.io.IOException;
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
            if (field.getValue().isJsonArray()) {

            } else if (field.getValue().isJsonPrimitive()) {
                JsonPrimitive primitive = field.getValue().getAsJsonPrimitive();
                if (primitive.isString()) {
                    ret.put(field.getKey(), primitive.getAsString());
                } else if (primitive.isNumber()) {
                    ret.put(field.getKey(), primitive.getAsInt());
                }
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

    static class IntegerArrayListAdapter extends TypeAdapter<ArrayList<Integer>> {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();

        @Override
        public void write(JsonWriter jsonWriter, ArrayList<Integer> ints) throws IOException {
            if (ints == null) {
                jsonWriter.beginArray().nullValue().endArray();
                return;
            }

            JsonWriter objectWriter = jsonWriter.beginArray();
            for (Integer t : ints) {
                objectWriter.value(t);
            }
            jsonWriter.endArray();
        }

        @Override
        public ArrayList<Integer> read(JsonReader jsonReader) throws IOException {
            ArrayList<Integer> ret = new ArrayList<Integer>();
            if (jsonReader.peek() != JsonToken.BEGIN_ARRAY) {
                return new ArrayList<Integer>();
            }

            return ret;


        }
    }



}
