package com.peck.android.network;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.peck.android.PeckApp;
import com.peck.android.PeckApp.Constants.Network;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.LocaleManager;
import com.peck.android.models.Circle;
import com.peck.android.models.Event;
import com.peck.android.models.Food;
import com.peck.android.models.Locale;
import com.peck.android.models.Meal;
import com.peck.android.models.Peck;
import com.peck.android.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by mammothbane on 6/26/2014.
 */
public class ServerCommunicator implements Singleton {
    private static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
    private static RequestQueue requestQueue = PeckApp.getRequestQueue();
    private static JsonParser parser = new JsonParser();

    private static ServerCommunicator serverCommunicator = new ServerCommunicator();

    private static final HashMap<Class<? extends DBOperable>, String> apiMap =
            new HashMap<Class<? extends DBOperable>, String>();

    static {
        apiMap.put(Event.class, Network.EVENTS);
        apiMap.put(Circle.class, Network.CIRCLES);
        apiMap.put(Locale.class, Network.LOCALES);
        apiMap.put(Meal.class, Network.MEAL);
        apiMap.put(Food.class, Network.FOOD);
        apiMap.put(Peck.class, Network.PECK);
        apiMap.put(User.class, Network.USERS);
    }


    private ServerCommunicator() { }

    private static ServerCommunicator getCommunicator() {
        return serverCommunicator;
    }

    public static <T extends DBOperable> JSONObject toJson(T obj, Class<T> tClass) throws JSONException {
        Locale locale = LocaleManager.getManager().getLocale();
        if (locale == null) {

                /* todo: throw an error dialog to the user/put them in locale selection */

            //TEST:
            locale = new Locale();
            locale.setServerId(50);
        }

        JSONObject object = new JSONObject(gson.toJson(obj, tClass));
        object.put(PeckApp.Constants.Network.INSTITUTION, LocaleManager.getManager().getLocale());
        return object;
    }

    public static <T extends DBOperable> void getObject(int serverId, Class<T> tClass, final Callback<T> callback) {
        String url = PeckApp.Constants.Network.API_STRING + apiMap.get(tClass) + serverId;

        get(tClass, new Callback<ArrayList<T>>() {
            @Override
            public void callBack(ArrayList<T> obj) {
                callback.callBack(obj.get(0));
            }
        }, url);

    }

    public static <T extends DBOperable> void getAll(final Class<T> tClass, final Callback<ArrayList<T>> callback) {
        String url = PeckApp.Constants.Network.API_STRING + apiMap.get(tClass);
        get(tClass, callback, url);
    }

    private static <T extends DBOperable> void get(final Class<T> tClass, final Callback<ArrayList<T>> callback, final String url) {
        requestQueue.add(new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                callback.callBack(parseJson(parser.parse(object.toString()), tClass));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }));
    }


    private static <T extends DBOperable> ArrayList<T> parseJson(JsonElement obj, Class<T> tClass) {
        ArrayList<T> ret = new ArrayList<T>();
        if (obj.isJsonObject()) {
            if (wrapsJsonElement((JsonObject) obj)) {
                ret.addAll(parseJson(((JsonObject)obj).entrySet().iterator().next().getValue(), tClass));
            } else ret.add(gson.fromJson(obj, tClass));
        } else if (obj.isJsonArray()) {
            for (JsonElement arrayElement : (JsonArray)obj) {
                ret.addAll(parseJson(arrayElement, tClass));
            }
        } //if it's none of those, it's a jsonnull or a primitive, and we don't handle either of those, maybe todo: throw an exception
        return ret;
    }

    private static boolean wrapsJsonElement(JsonObject object) {
        return (object.entrySet().size() == 1);
    }



    public static <T extends DBOperable> void postObject(final T post, Class<T> tClass) {
        try {

            JSONObject item = toJson(post, tClass);

            requestQueue.add(new JsonObjectRequest(Request.Method.POST, PeckApp.Constants.Network.API_STRING + PeckApp.Constants.Network.EVENTS, item, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject object) {
                    //todo: update post object with returned object server id
                    Log.d(getClass().getSimpleName(), object.toString());
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(getClass().getSimpleName(), volleyError.toString());
                }

            }));

        } catch (JSONException e) { e.printStackTrace(); }


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
