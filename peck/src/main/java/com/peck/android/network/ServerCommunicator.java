package com.peck.android.network;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.peck.android.PeckApp;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.DBOperable;
import com.peck.android.interfaces.Singleton;
import com.peck.android.managers.LocaleManager;
import com.peck.android.models.Locale;
import com.peck.android.network.NetworkSpec.NetworkSpec;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mammothbane on 6/26/2014.
 */
public class ServerCommunicator implements Singleton {
    private static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private static RequestQueue requestQueue = PeckApp.getRequestQueue();
    private static JsonParser parser = new JsonParser();

    private static ServerCommunicator serverCommunicator = new ServerCommunicator();

    private ServerCommunicator() { }

    private static ServerCommunicator getCommunicator() {
        return serverCommunicator;
    }

    public static <T> JSONObject toJson(T obj, NetworkSpec<T> spec) throws JSONException {
        Locale locale = LocaleManager.getManager().getLocale();
        if (locale == null) {

                /* todo: throw an error dialog to the user/put them in locale selection */

            //TEST:
            locale = new Locale();
            locale.setServerId(50);
        }

        JSONObject object = new JSONObject(gson.toJson(obj, spec.getType()));
        object.put(PeckApp.Constants.Network.INSTITUTION, LocaleManager.getManager().getLocale());
        return object;
    }

    public static <T> void getObject(int serverId, NetworkSpec<T> spec, final Callback<T> callback) {
        String url = PeckApp.Constants.Network.API_STRING + spec.getApiExtension() + serverId;

        requestQueue.add(new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }));

    }

    public static <T> void getAll(final NetworkSpec<T> spec, final Callback<ArrayList<T>> callback) {
        String url = PeckApp.Constants.Network.API_STRING + spec.getApiExtension();

        requestQueue.add(new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {

                JSONObject jsonObject = new JSONObject();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }));

    }

    private static void parseJson(JSONObject jsonObject) {
        String s;
        try {
            while (jsonObject.keys().hasNext()) {
                s = (String) jsonObject.keys().next();
                JsonElement ret = parser.parse(jsonObject.get(s).toString());

            }
        } catch (JSONException e) { e.printStackTrace(); } //todo: give an error message about not being able to parse the json
    }



    public static <T extends DBOperable> void postObject(final T post, NetworkSpec<T> spec) {
        try {

            JSONObject item = toJson(post, spec);

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

}
