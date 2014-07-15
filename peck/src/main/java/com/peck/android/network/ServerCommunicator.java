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
import com.peck.android.R;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.LocaleManager;
import com.peck.android.models.DBOperable;

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

    private ServerCommunicator() { }

    public static <T extends DBOperable> JSONObject toJson(T obj, Class<T> tClass) throws JSONException {
        int locale = LocaleManager.getLocale();
        if (locale == 0) {
                /* todo: throw an error dialog to the user/put them in locale selection */

            locale = 1;
        }

        JsonObject object = (JsonObject)gson.toJsonTree(obj, tClass); //take our object and JSONize it
        object.addProperty(PeckApp.Constants.Network.INSTITUTION, locale);

        JsonObject ret = new JsonObject(); //wrap it in another object
        ret.add(apiMap.get(tClass).substring(0, apiMap.get(tClass).length() - 2), object);
        ret.add("auth", authBlock());

        return new JSONObject(ret.toString());
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

    private static JsonObject authBlock() {
        JsonObject auth = new JsonObject();
        //auth.addProperty("user_id", PeckSessionHandler.getUser().getServerId());
        auth.addProperty("api_key", "");
        auth.addProperty("auth_token", "");
        auth.addProperty("", "");
        return auth;
    }

    public static <T extends DBOperable> void getObject(int serverId, Class<T> tClass, final Callback<T> callback, final Callback failure) {
        String url = PeckApp.Constants.Network.ENDPOINT + apiMap.get(tClass) + serverId;

        get(tClass, new Callback<ArrayList<T>>() {
            @Override
            public void callBack(ArrayList<T> obj) {
                callback.callBack(obj.get(0));
            }
        }, failure, url);

    }

    public static <T extends DBOperable> void getAll(final Class<T> tClass, final Callback<ArrayList<T>> callback, final Callback failure) {
        String url = PeckApp.Constants.Network.ENDPOINT + apiMap.get(tClass);
        get(tClass, callback, failure, url);
    }

    private static <T extends DBOperable> void get(final Class<T> tClass, final Callback<HashMap<Integer, JsonObject>> callback, final Callback failure, final String url) {
        Log.v(ServerCommunicator.class.getSimpleName() + ": " + tClass.getSimpleName(), "sending GET to " + url);

        requestQueue.add(new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v(ServerCommunicator.class.getSimpleName() + ": " + tClass.getSimpleName(), "received response from " + url);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));

        requestQueue.add(new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                Log.v(ServerCommunicator.class.getSimpleName() + ": " + tClass.getSimpleName(), "received response from " + url);
                callback.callBack(parseJson(parser.parse(object.toString()), tClass));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                failure.callBack(null);
            }
        }));
    }

    private static boolean wrapsJsonElement(JsonObject object) {
        return (object.entrySet().size() == 1);
    }

    public static <T extends DBOperable> void postObject(final T post, Class<T> tClass) {
        postObject(post, tClass, new Callback.NullCb(), new Callback.NullCb());
    }

    public static <T extends DBOperable> void postObject(final T post, final Class<T> tClass, final Callback<T> callback, final Callback<VolleyError> failure) {
        sendObject(Request.Method.POST, post, tClass, callback, failure);
    }

    public static <T extends DBOperable> void patchObject(final T patch, final Class<T> tClass, final Callback<T> callback, final Callback<VolleyError> failure) {
        sendObject(Request.Method.PATCH, patch, tClass, callback, failure);
    }

    private static <T extends DBOperable> void sendObject(final int method, final T post, final Class<T> tClass, final Callback<T> callback, final Callback<VolleyError> failure) {
        try {

            JSONObject item = toJson(post, tClass);

            Log.d(ServerCommunicator.class.getSimpleName() + ": " + tClass.getSimpleName(), (method == Request.Method.PATCH) ? "PATCH" : "POST" + " item " + item.toString());
            requestQueue.add(new JsonObjectRequest(method, PeckApp.Constants.Network.ENDPOINT + apiMap.get(tClass), item, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject object) {
                    callback.callBack(gson.fromJson(object.toString(), tClass));
                    Log.d(ServerCommunicator.class.getSimpleName() + ": " + tClass.getSimpleName(), "response with item " + object.toString());
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    failure.callBack(volleyError);
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
