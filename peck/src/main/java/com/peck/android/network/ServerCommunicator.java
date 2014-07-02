package com.peck.android.network;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
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
import com.peck.android.R;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.LocaleManager;
import com.peck.android.models.Circle;
import com.peck.android.models.DBOperable;
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

    public static <T extends DBOperable> JSONObject toJson(T obj, Class<T> tClass) throws JSONException {
        Locale locale = LocaleManager.getManager().getLocale();
        if (locale == null) {
                /* todo: throw an error dialog to the user/put them in locale selection */

            //TEST:
            locale = new Locale();
            locale.setServerId(1);
        }
        JsonObject object = (JsonObject)gson.toJsonTree(obj, tClass); //take our object and JSONize it
        object.addProperty(PeckApp.Constants.Network.INSTITUTION, locale.getServerId());

        JsonObject ret = new JsonObject(); //wrap it in another object
        ret.add(apiMap.get(tClass).substring(0, apiMap.get(tClass).length() - 2), object); //subtract 2 for the trailing slash and the s

        return new JSONObject(ret.toString());
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
        Log.v(ServerCommunicator.class.getSimpleName() + ": " + tClass.getSimpleName(), "sending GET to " + url);
        requestQueue.add(new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                Log.v(ServerCommunicator.class.getSimpleName() + ": " + tClass.getSimpleName(), "received response from " + url);
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
        postObject(post, tClass, new Callback<T>() {public void callBack(T obj) {} });
    }

    public static <T extends DBOperable> void postObject(final T post, final Class<T> tClass, final Callback<T> callback) {
        sendObject(Request.Method.POST, post, tClass, callback);
    }

    public static <T extends DBOperable> void patchObject(final T patch, final Class<T> tClass, final Callback<T> callback) {
        sendObject(Request.Method.PATCH, patch, tClass, callback);
    }

    private static <T extends DBOperable> void sendObject(final int method, final T post, final Class<T> tClass, final Callback<T> callback) {
        try {

            JSONObject item = toJson(post, tClass);

            Log.d(ServerCommunicator.class.getSimpleName() + ": " + tClass.getSimpleName(), (method == Request.Method.PATCH) ? "PATCH" : "POST" + " item " + item.toString());
            requestQueue.add(new JsonObjectRequest(method, PeckApp.Constants.Network.API_STRING + apiMap.get(tClass), item, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject object) {
                    callback.callBack(gson.fromJson(object.toString(), tClass));
                    Log.d(ServerCommunicator.class.getSimpleName() + ": " + tClass.getSimpleName(), "response with item " + object.toString());
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(getClass().getSimpleName(), volleyError.toString());
                }

            }));

        } catch (JSONException e) { e.printStackTrace(); }

    }

    public static void getImage(final String URL, int dimens, final Callback<Bitmap> callback) {
        PeckApp.getRequestQueue().add(new ImageRequest(URL, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                callback.callBack(bitmap);
            }
        }, dimens, dimens, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(PeckApp.getContext(), "Network error. Couldn't get image from " + URL, Toast.LENGTH_LONG).show();
                callback.callBack(null);
            }
        }));
    }

    public static void getImage(final String URL, Callback<Bitmap> callback) {
        getImage(URL, (int) PeckApp.getContext().getResources().getDimension(R.dimen.prof_picture_bound), callback);
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
