package com.peck.android.network;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.peck.android.PeckApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by mammothbane on 7/22/2014.
 */
public class ServerCommunicator {
    private static JsonObject send(String url, int method, JsonObject data) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(method, url, (data != null) ? new JSONObject(data.toString()) : null, future, future);

        PeckApp.getRequestQueue().add(request);
        return ((JsonObject)new JsonParser().parse(future.get().toString()));
    }

    public static JsonObject get(String url) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        return send(url, Request.Method.GET, null);
    }

    public static JsonObject post(String url, JsonObject object) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        return send(url, Request.Method.POST, object);
    }

    public static JsonObject patch(String url, JsonObject object) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        return send(url, Request.Method.PATCH, object);
    }

    public static boolean delete(String url, JsonObject object) throws InterruptedException, ExecutionException, JSONException, VolleyError {
        send(url, Request.Method.DELETE, null);
        //todo: what happens when we run a delete? how do we return?
        return true;
    }
}
