package com.peck.android.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;


/**
 * Created by mammothbane on 6/20/2014.
 */
public class JsonHandler<T> {

    Type type = new TypeToken<T>() {}.getType();
    private final String TAG = (getClass().getName() + ": " + type.toString());
    private Gson gson = new Gson();

    public T get(JsonElement json) {
        return gson.fromJson(json, type);
    }

    public T get(File file) throws FileNotFoundException {
        //file must be pure json
        JsonParser jsonParser = new JsonParser();

        return gson.fromJson(jsonParser.parse(new FileReader(file)), type);
    }

    public String put(T t) {
        return gson.toJson(t, type);
    }

}
